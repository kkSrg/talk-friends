package com.tanhua.api.app;

import com.tanhua.mongo.pojo.Movement;

import java.util.List;
import java.util.Map;

public interface MovementApi {
    /**
     * 1、查询用户动态
     * @param userIds
     * @return
     */
    List<Movement> findByUserIds(List<Object> userIds,Integer page, Integer pagesize);

    List<Movement> findByPids(List<Long> redisPids,Integer page, Integer pagesize);

    void save(Movement movement);


    /**
     * 点赞
     * @param movementId
     */
    void like(String movementId,Long id);
    /**
     * 点赞数
     * @param movementId
     */
    Long likeCount(String movementId);

    /**
     * 是否点赞
     * @param movementId
     * @param id
     * @return
     */
    Boolean isLike(String movementId,Long id);

    /**
     * 5、取消点赞操作
     * @param movementId  当前点赞的动态id
     */
    void disLike(String movementId, Long id);

    /**
     * 6、喜欢操作
     * @param movementId  当前点赞的动态id
     */
    void love(String movementId, Long id);
    /**
     * 7、取消喜欢操作
     * @param movementId  当前点赞的动态id
     */
    void unLove(String movementId, Long id);

    /**
     * 喜欢数
     * @param movementId
     */
    Long loveCount(String movementId);
    /**
     * 是否喜欢
     *
     * @param movementId
     * @param id
     */
    Boolean isLove(String movementId, Long id);

    /**
     * 评论数
     * @param movementId
     */
    Long commentCount(String movementId);
    /**
     * 8、根据动态id查询动态信息
     * @param movementId
     */
    Movement findOneMovement(String movementId);

    //void saveComment(String movementId, Long id, String content, CommentType type);

    /**
     * 发表评论
     * @param commentId
     * @param content
     * @param id
     */
    void comment(String commentId,String content,Long id);

    /**
     * 当前佳人动态
     * @return
     */
    List<Movement> findAllByUserId(Long userId, Integer page, Integer pagesize);
}
