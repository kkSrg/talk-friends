package com.tanhua.app.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.api.app.MovementApi;
import com.tanhua.api.app.SmallVideoApi;
import com.tanhua.api.sso.UserInfoServiceApi;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.db.pojo.UserInfo;
import com.tanhua.enums.IdType;
import com.tanhua.exception.ConsumerException;
import com.tanhua.mongo.PageResult;
import com.tanhua.mongo.pojo.Video;
import com.tanhua.mongo.vo.CommentVo;
import com.tanhua.mongo.vo.VideoVo;
import com.tanhua.utils.ThreadLocalUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SmallVideosService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @DubboReference
    private SmallVideoApi smallVideoApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @DubboReference
    private MovementApi movementApi;

    @Autowired
    private MessagesService messagesService;

    /**
     * 1. 查询小视频列表
     * 如果有推荐视频，优先返回推荐视频，如果没有，按照时间倒序查询视频表
     * @param page
     * @param pagesize
     */
    public PageResult<VideoVo> findVideos(Integer page, Integer pagesize) {
        Long uid = ThreadLocalUtil.getId();

        PageResult<VideoVo> listPageResult = new PageResult<>();
        listPageResult.setPage(page);
        listPageResult.setPagesize(pagesize);
        //1. 从redis中获取推荐列表
        String rediskey = "VIDEOS_RECOMMEND_" + uid;
        String redisData = stringRedisTemplate.opsForValue().get(rediskey);
        List<Video> videoList = new ArrayList<>();
        //2. 判断redisData中是否有数据
        //声明redis中分页的定位页数
        int redisPages = 0;
        if (StrUtil.isNotBlank(redisData)){
            String[] vids = StrUtil.split(redisData, ",");
            //判断redis中的下一页是否有数据
            if ((page - 1)*pagesize < vids.length){
                //将vid转换为整型
                List<Long> vidsList = Arrays.stream(vids).skip((page-1)*pagesize).limit(pagesize).map(vid ->{
                    return Convert.toLong(vid);
                }).collect(Collectors.toList());
                videoList = smallVideoApi.findByVids(vidsList,page,pagesize);
            }
            //redis中所占用的总页数
            redisPages= PageUtil.totalPage(vids.length,pagesize);
        }


        if (CollUtil.isEmpty(videoList)){
            //从mongodb中获取
            videoList=smallVideoApi.findAll(page-redisPages,pagesize);
        }
        List<VideoVo> list = videoList.stream().map(video -> {
            VideoVo vo = new VideoVo();
            BeanUtil.copyProperties(video,vo);
            vo.setCover(video.getPicUrl());
            vo.setSignature(video.getText());
            UserInfo userInfo = userInfoServiceApi.findById(video.getUserId());
            BeanUtil.copyProperties(userInfo,vo,"id");


            vo.setLikeCount(Convert.toInt(movementApi.likeCount(video.getId().toHexString())));//获取点赞数
            vo.setHasLiked(movementApi.isLike(video.getId().toHexString(),uid)?1:0);//是否标记点赞
            //TODO 互动数据
            vo.setHasFocus(movementApi.isLove(video.getId().toHexString(),uid)?1:0); //是否关注
            vo.setCommentCount(Convert.toInt(movementApi.commentCount(video.getId().toHexString())));//评论数量

            return vo;
        }).collect(Collectors.toList());
        listPageResult.setItems(list);

        return listPageResult;
    }

    /**
     * 2. 视频点赞操作
     * @param vid 视频的id
     */
    public void like(String vid) {
        Long uid = ThreadLocalUtil.getId();
        movementApi.like(vid,uid);
    }

    /**
     * 3. 取消点赞
     * @param vid
     */
    public void disLike(String vid) {
        Long uid = ThreadLocalUtil.getId();
        movementApi.disLike(vid,uid);
    }

    /**
     * 8. 视频用户关注
     * @param uid 被关注的用户
     */
    public void userFocus(String uid) {
        //获取登录者id(关注者)
        Long id = ThreadLocalUtil.getId();
        smallVideoApi.saveFocus(uid,id);
        //查询被关注者是否关注当前登录者
        Boolean flag = smallVideoApi.isFocus(Convert.toLong(uid), id);
        //如果为true则表示互相关注,则添加为好友
        if (flag){
            messagesService.contacts(Convert.toLong(uid));
        }
    }


    /**
     * 9. 视频用户取消关注
     * @param uid
     */
    public void userUnFocus(String uid) {
        //获取登录者id(关注者)
        Long id = ThreadLocalUtil.getId();
        Boolean flag = smallVideoApi.removeFocus(Convert.toLong(uid), id);
        Boolean focus = smallVideoApi.isFocus(Convert.toLong(uid), id);
        if (flag && focus){
            messagesService.unContacts(Convert.toLong(uid));
        }
    }

    @Autowired
    private OssTemplate ossTemplate;
    @Autowired
    private FastFileStorageClient client; //从调度服务器获取，一个目标存储服务器，上传
    @Autowired
    private FdfsWebServer webServer;// 获取存储服务器的请求URL
    @Autowired
    private IdService idService;
    /**
     * 发布视频
     * @param videoThumbnail 封面图片
     * @param videoFile  视频
     */
    public void uploadVideo(MultipartFile videoFile, MultipartFile videoThumbnail) {
        try {
            //1. 上传封面图片到oss,并获取存储路径
            String picPath = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
            //2. 上传视频到fastDFS,并获取存储路径
            String originalFilename = videoFile.getOriginalFilename();
            String suffixName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            StorePath storePath = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), suffixName, null);
            String videoPath = webServer.getWebServerUrl() + storePath.getPath();
            //3. 封装需要保存的对象
            Video video = new Video();
            video.setPicUrl(picPath);
            video.setVideoUrl(videoPath);
            video.setUserId(ThreadLocalUtil.getId());
            video.setCreated(System.currentTimeMillis());
            video.setVid(idService.createId(IdType.VIDEO));
            video.setText("我在合肥等你! !");
            //4. 执行保存
            smallVideoApi.saveVideo(video);
        } catch (IOException e) {
            throw new ConsumerException("视频上传失败");
        }
    }
}
