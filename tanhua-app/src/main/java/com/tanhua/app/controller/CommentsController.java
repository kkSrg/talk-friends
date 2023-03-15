package com.tanhua.app.controller;

import com.tanhua.app.service.CommentsService;
import com.tanhua.mongo.PageResult;
import com.tanhua.mongo.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;
    /**
     * 1、评论列表
     * @param movementId
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping
    public ResponseEntity<PageResult<CommentVo>> comments(String movementId,
                                                          @RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                                          @RequestParam(value = "pageszie",required = false,defaultValue = "10")Integer pagesize){
        PageResult<CommentVo> result=commentsService.comments(movementId,page,pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 2、发表评论
     */
    @PostMapping
    public ResponseEntity saveComment(@RequestBody Map<String,String> params){
        String movementId=params.get("movementId");
        String content=params.get("comment");
        commentsService.saveComment(movementId,content);
        return ResponseEntity.ok(null);
    }

    /**
     * 评论的点赞操作
     * @param commentId
     * @return
     */
    @GetMapping("{id}/like")
    public ResponseEntity<Long> like(@PathVariable("id") String commentId){
        Long count=commentsService.like(commentId);
        return ResponseEntity.ok(count);
    }

    /**
     * 评论的取消点赞操作
     * @param commentId
     * @return
     */
    @GetMapping("{id}/dislike")
    public ResponseEntity<Long> disLike(@PathVariable("id") String commentId){
        Long count=commentsService.disLike(commentId);
        return ResponseEntity.ok(count);
    }
}
