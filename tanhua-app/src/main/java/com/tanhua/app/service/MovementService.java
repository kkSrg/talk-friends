package com.tanhua.app.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import com.tanhua.api.app.FriendApi;
import com.tanhua.api.app.MovementApi;
import com.tanhua.api.sso.UserInfoServiceApi;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.db.pojo.UserInfo;
import com.tanhua.enums.IdType;
import com.tanhua.mongo.PageResult;
import com.tanhua.mongo.dto.MovementDto;
import com.tanhua.mongo.pojo.Friend;
import com.tanhua.mongo.pojo.Movement;
import com.tanhua.mongo.vo.MovementVo;
import com.tanhua.utils.RelativeDateFormat;
import com.tanhua.utils.ThreadLocalUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovementService {


    @DubboReference
    private FriendApi friendApi;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 1、查询好友动态
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<MovementVo> movements(Integer page, Integer pagesize) {
        //1、获取当前登陆者id
        Long id = ThreadLocalUtil.getId();
        //2、使用id查询其好友id:    friend集合
        List<Friend> friendList = friendApi.findByFriendId(id);
        //3、从friendList获取所有的userId
        //List<Long> userIds=friendList.stream().map(friend -> {return friend.getUserId();}).collect(Collectors.toList());
        List<Object> userIds = CollUtil.getFieldValues(friendList, "friendId");
        PageResult<MovementVo> result=new PageResult<>();
        result.setPage(page);
        result.setPagesize(pagesize);
        if (CollUtil.isEmpty(userIds)){
            return result;
        }
        //6、组装响应的结果对象MovementVo
        List<MovementVo> list=new ArrayList<>();
        //4、查询好友发布的动态  1,2,3,4
        List<Movement> movementList = movementApi.findByUserIds(userIds,page,pagesize);
        //5、查询好友的用户信息
        List<UserInfo>  userInfoList=userInfoServiceApi.findByIds(userIds);
        result.setItems(fillBean(movementList,userInfoList));//返回数据集合
        return result;
    }

    @Value("${defaultPids}")
    private String defaultPids;
    /**
     * 2、查询推荐动态
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<MovementVo> recommend(Integer page, Integer pagesize) {
        //1、获取当前登陆者id
        Long id = ThreadLocalUtil.getId();
        //2、使用id到redis中获取推荐的pid
        String redisKey="MOVEMENTS_RECOMMEND_"+id;
        //"10127,10020,20,23,10064,10092,17,16,10067,10081,19,27,26,22,21,10093,18,25,10015,24"
        String redisPid = stringRedisTemplate.opsForValue().get(redisKey);
        //如果redis中没有推荐的动态，那么我们给个默认的动态作为推荐动态
        if(StrUtil.isBlank(redisPid)){
           // redisPid="16,17,18,19,20,21,22,23,24,25";
            redisPid=defaultPids;
        }


        //[10127,10020,20,23,10064,10092,17,16,10067,10081,19,27,26,22,21,10093,18,25,10015,24]
        String[] redisPids = StrUtil.split(redisPid, ",");
        //将字符串pid转换冲Long存储在list集合
        List<Long> pidList=Arrays.stream(redisPids).map(s->{
            return Convert.toLong(s);
        }).collect(Collectors.toList());
        //3、查询mongo获取推荐的动态
        List<Movement> movementList= movementApi.findByPids(pidList,page,pagesize);

        //如果没有数据则直接返回
        PageResult<MovementVo> result=new PageResult<>();
        result.setPage(page);
        result.setPagesize(pagesize);
        if (CollUtil.isEmpty(movementList)){
            return result;
        }

        //4、从movementList获取所有userId
        List<Object> userIds = CollUtil.getFieldValues(movementList, "userId");
        //5、查询好友的用户信息
        List<UserInfo>  userInfoList=userInfoServiceApi.findByIds(userIds);
        if (CollUtil.isNotEmpty(userIds)){
            result.setItems(fillBean(movementList,userInfoList));//返回数据集合
        }
        return result;
    }

    //抽取公共复制方法
    private List<MovementVo> fillBean(List<Movement> movementList,List<UserInfo> userInfoList){
        //6、组装响应的结果对象MovementVo
        List<MovementVo> list=new ArrayList<>();
        for (Movement movement : movementList) {//1
            for (UserInfo userInfo : userInfoList) {

                if (movement.getUserId()== userInfo.getId()){
                    MovementVo vo=new MovementVo();
                    BeanUtil.copyProperties(movement,vo);
                    //处理未copy的属性值
                    vo.setImageContent(movement.getMedias());
                    vo.setDistance("1公里");
                    //处理显示时间问题  xxxx前
                    vo.setCreateDate(RelativeDateFormat.format(new Date(movement.getCreated())));
                    //是否点赞
                    vo.setHasLiked(movementApi.isLike(movement.getId().toHexString(),ThreadLocalUtil.getId())?1:0);
                    //点赞数
                    vo.setLikeCount(Convert.toInt(movementApi.likeCount(movement.getId().toHexString())));
                    //是否喜欢
                    vo.setHasloved(movementApi.isLove(movement.getId().toHexString(),ThreadLocalUtil.getId())?1:0);
                    //喜欢数
                    vo.setLoveCount(Convert.toInt(movementApi.loveCount(movement.getId().toHexString())));
                    //评论数
                    vo.setCommentCount(Convert.toInt(movementApi.commentCount(movement.getId().toHexString())));
                    BeanUtil.copyProperties(userInfo,vo,"id");
                    //处理未copy的属性值
                    vo.setTags(StrUtil.split(userInfo.getTags(),","));
                    //将组装的数据结果存储到集合
                    list.add(vo);
                }
            }
        }
        return list;
    }

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private IdService idService;
    /**
     * 3、发布动态
     * @param imageContent
     * @param dto
     * @return
     */
    public void saveMovement(MultipartFile[] imageContent, MovementDto dto) {

        List<String> pathList=new ArrayList<>();
        if (imageContent !=null && imageContent.length>0){
            //1、实现图片上传
            for (MultipartFile file : imageContent) {
                try {
                    String uploadPath = ossTemplate.upload(file.getOriginalFilename(), file.getInputStream());
                    pathList.add(uploadPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //最好改下Movement实体类，改完之后要查看之前使用的地方，对其做响应的更改
       /* Object[] array = pathList.toArray();
        String[] medias=new String[array.length];
        for (int i=0;i<array.length;i++) {
            medias[i]=Convert.toStr(array[i]);
        }*/

        //pid的key  increment_pid
        //封装数据  userId?  pid?  好友怎么能知道我发布了动态呢？
        Movement movement=new Movement();
        BeanUtil.copyProperties(dto,movement);
        movement.setLocationName(dto.getLocation());
        movement.setMedias(pathList);
        movement.setUserId(ThreadLocalUtil.getId());
        movement.setPid(idService.createId(IdType.MOVEMENT));
        //远程调用保存操作
        movementApi.save(movement);
        /**
         * 异步实现步骤：
         *  1、在启动类上开启异步支持
         *  2、在调用的方法上使用异步注解
         */
        idService.testAsync();
        System.out.println("我执行完事！！！");
    }

    /**
     * 4、点赞操作
     * @param movementId  当前点赞的动态id
     * @return     点赞数
     */
    public Long like(String movementId) {
        //获取当前登录者id
        Long id = ThreadLocalUtil.getId();
        //点赞
        movementApi.like(movementId,id);
        return Convert.toLong(movementApi.likeCount(movementId));
    }

    /**
     * 5、取消点赞操作
     * @param movementId  当前点赞的动态id
     * @return     点赞数
     */
    public Long disLike(String movementId) {
        //获取当前登录者id
        Long id = ThreadLocalUtil.getId();
        //点赞
        movementApi.disLike(movementId,id);
        return Convert.toLong(movementApi.likeCount(movementId));
    }
    /**
     * 6、喜欢操作
     * @param movementId  当前点赞的动态id
     * @return     点赞数
     */
    public Long love(String movementId) {
        //获取当前登录者id
        Long id = ThreadLocalUtil.getId();
        //点赞
        movementApi.love(movementId,id);
        return Convert.toLong(movementApi.loveCount(movementId));
    }
    /**
     * 7、取消喜欢操作
     * @param movementId  当前点赞的动态id
     * @return     点赞数
     */
    public Long unLove(String movementId) {
        //获取当前登录者id
        Long id = ThreadLocalUtil.getId();
        //点赞
        movementApi.unLove(movementId,id);
        return Convert.toLong(movementApi.loveCount(movementId));
    }
    /**
     * 8、根据动态id查询动态信息
     * @param movementId
     * @return
     */
    public MovementVo findOneMovement(String movementId) {
        Movement movement=movementApi.findOneMovement(movementId);
        UserInfo userInfo = userInfoServiceApi.findById(movement.getUserId());

        MovementVo vo=new MovementVo();
        BeanUtil.copyProperties(movement,vo);
        //处理未copy的属性值
        vo.setImageContent(movement.getMedias());
        //评论数
        vo.setCommentCount(Convert.toInt(movementApi.commentCount(movement.getId().toHexString())));
        BeanUtil.copyProperties(userInfo,vo,"id");
        //处理未copy的属性值
        vo.setTags(StrUtil.split(userInfo.getTags(),","));
        return vo;
    }

    /**
     * 10. 当前佳人动态
     * @param page
     * @param pagesize
     */
    public PageResult<MovementVo> all(Long userId, Integer page, Integer pagesize) {
        // 根据userId查询动态
        List<Movement> movementList = movementApi.findAllByUserId(userId,page,pagesize);
        PageResult<MovementVo> result = new PageResult<>();
        result.setCounts(100);
        result.setPages(100);
        result.setPage(page);
        result.setPagesize(pagesize);
        if (CollUtil.isEmpty(movementList)){
            return result;
        }
        // 根据userId查询个人信息
        UserInfo userInfo = userInfoServiceApi.findById(userId);
        List<UserInfo> userInfoList = new ArrayList<>();
        userInfoList.add(userInfo);
        List<MovementVo> list = fillBean(movementList, userInfoList);

        result.setItems(list);
        return result;
    }
}
