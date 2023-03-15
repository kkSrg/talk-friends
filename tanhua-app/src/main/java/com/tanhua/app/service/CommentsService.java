package com.tanhua.app.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.tanhua.api.app.CommentsApi;
import com.tanhua.api.app.MovementApi;
import com.tanhua.api.sso.UserInfoServiceApi;
import com.tanhua.db.pojo.UserInfo;
import com.tanhua.enums.CommentType;
import com.tanhua.mongo.PageResult;
import com.tanhua.mongo.pojo.Comment;
import com.tanhua.mongo.vo.CommentVo;
import com.tanhua.utils.ThreadLocalUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentsService {

    @DubboReference
    private CommentsApi commentsApi;
    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;
    @DubboReference
    private MovementApi movementApi;

    /**
     * 1、评论列表查询
     * @param movementId
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult<CommentVo> comments(String movementId, Integer page, Integer pagesize) {
        //1、根据评论的类型和动态id查询对应的评论列表
        List<Comment> commentList=commentsApi.findAllByPublishId(movementId,page,pagesize);
        PageResult<CommentVo> result=new PageResult<>();
        result.setCounts(100);
        result.setPages(100);
        result.setPage(page);
        result.setPagesize(pagesize);
        if (CollUtil.isEmpty(commentList)){
            //如果没有查到数据,此次result中的集合数据也不能为null,给一个空集合
            result.setItems(new ArrayList<>());
            return  result;
        }

        List<CommentVo> commentVoList=commentList.stream().map(comment -> {
            //获取评论者id
            Long userId = comment.getUserId();
            UserInfo userInfo = userInfoServiceApi.findById(userId);
            //组装返回数据对象
            CommentVo vo=new CommentVo();
            vo.setId(comment.getId().toHexString());//评论id
            vo.setAvatar(userInfo.getAvatar());
            vo.setNickname(userInfo.getNickname());
            vo.setContent(comment.getContent());//评论内容
            vo.setCreateDate(DateUtil.formatDate(new Date(comment.getCreated())));
            vo.setLikeCount(Convert.toInt(movementApi.likeCount(comment.getId().toHexString())));//查询的是评论
            vo.setHasLiked(movementApi.isLike(comment.getId().toHexString(), ThreadLocalUtil.getId())?1:0);
            return vo;

        }).collect(Collectors.toList());
        result.setItems(commentVoList);
        return result;
    }


    /**
     * 2、发表评论
     */
    public void saveComment(String commentId,String content) {
        movementApi.comment(commentId,content,ThreadLocalUtil.getId());
    }

    /**
     * 评论的点赞操作
     * @param commentId
     * @return
     */
    public Long like(String commentId) {
        //获取当前登录者id
        Long id = ThreadLocalUtil.getId();
        //点赞
        movementApi.like(commentId,id);
        return Convert.toLong(movementApi.likeCount(commentId));
    }

    /**
     * 评论的取消点赞操作
     * @param commentId
     * @return
     */
    public Long disLike(String commentId) {
        //获取当前登录者id
        Long id = ThreadLocalUtil.getId();
        //点赞
        movementApi.disLike(commentId,id);
        return Convert.toLong(movementApi.likeCount(commentId));
    }
}
