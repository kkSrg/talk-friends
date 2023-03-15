package com.tanhua.app.controller;

import com.tanhua.api.app.SmallVideoApi;
import com.tanhua.app.service.CommentsService;
import com.tanhua.app.service.SmallVideosService;
import com.tanhua.mongo.PageResult;
import com.tanhua.mongo.vo.CommentVo;
import com.tanhua.mongo.vo.VideoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("smallVideos")
public class SmallVideosController {

    @Autowired
    private SmallVideosService smallVideosService;

    @Autowired
    private CommentsService commentsService;



    /**
     * 1. 查询小视频列表
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping
    @Cacheable(
            value="videos",
            key = "T(com.tanhua.utils.ThreadLocalUtil).getId()+'_'+#page+'_'+#pagesize")
    public ResponseEntity<PageResult<VideoVo>> findVideos(@RequestParam(value = "page" ,required = false,defaultValue = "1") Integer page,
                                                          @RequestParam(value = "pagesize" ,required = false,defaultValue = "10") Integer pagesize){
        PageResult<VideoVo> result = smallVideosService.findVideos(page,pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 2. 视频点赞操作
     * @param id 视频的id
     * @return
     */
    @PostMapping("{id}/like")
    public ResponseEntity like(@PathVariable("id") String id){
        smallVideosService.like(id);
        return ResponseEntity.ok(null);
    }

    /**
     * 3. 取消点赞
     * @param id
     * @return
     */
    @PostMapping("{id}/dislike")
    public ResponseEntity disLike(@PathVariable("id") String id){
        smallVideosService.disLike(id);
        return ResponseEntity.ok(null);
    }

    /**
     * 4. 查看视频评论列表
     * @param page
     * @param pagesize
     * @param id
     * @return
     */
    @GetMapping("{id}/comments")
    public ResponseEntity<PageResult<CommentVo>> comments(@RequestParam(value = "page" ,required = false,defaultValue = "1") Integer page,
                                                          @RequestParam(value = "pagesize" ,required = false,defaultValue = "10") Integer pagesize,
                                                          @PathVariable("id") String id){

        PageResult<CommentVo> result = commentsService.comments(id,page,pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 5. 发布视频评论
     * @param id
     * @return
     */
    @PostMapping("{id}/comments")
    @CacheEvict(value="videos",allEntries = true)  //清空缓存
    public ResponseEntity<PageResult<CommentVo>> saveComment(@PathVariable("id") String id, @RequestBody Map<String,String> params){
        String content=params.get("comment");
        commentsService.saveComment(id,content);
        return ResponseEntity.ok(null);
    }

    /**
     * 6. 视频评论点赞
     * @param id 视频的id
     * @return
     */
    @PostMapping("comments/{id}/like")
    public ResponseEntity commentLike(@PathVariable("id") String id){
        smallVideosService.like(id);
        return ResponseEntity.ok(null);
    }

    /**
     * 7. 视频评论取消点赞
     * @param id 视频的id
     * @return
     */
    @PostMapping("comments/{id}/dislike")
    public ResponseEntity commentDisLike(@PathVariable("id") String id){
        smallVideosService.disLike(id);
        return ResponseEntity.ok(null);
    }

    /**
     * 8. 视频用户关注
     * @param uid
     * @return
     */
    @PostMapping("{uid}/userFocus")
    public ResponseEntity userFocus(@PathVariable("uid") String uid){
        smallVideosService.userFocus(uid);
        return ResponseEntity.ok(null);
    }

    /**
     * 9. 视频用户取消关注
     * @param uid
     * @return
     */
    @PostMapping("{uid}/userUnFocus")
    public ResponseEntity userUnFocus(@PathVariable("uid") String uid){
        smallVideosService.userUnFocus(uid);
        return ResponseEntity.ok(null);
    }

    /**
     * 10. 发布视频
     * @param videoThumbnail 封面图片
     * @param videoFile  视频
     * @return
     */
    @PostMapping
    public ResponseEntity uploadVideo(MultipartFile videoThumbnail,MultipartFile videoFile){
        smallVideosService.uploadVideo(videoFile,videoThumbnail);
        return ResponseEntity.ok(null);
    }
}
