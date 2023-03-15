package com.tanhua.dubbo.mg.api;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.tanhua.api.app.MovementApi;
import com.tanhua.enums.CommentType;
import com.tanhua.exception.ConsumerException;
import com.tanhua.mongo.pojo.Comment;
import com.tanhua.mongo.pojo.Movement;
import com.tanhua.mongo.pojo.Video;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Map;

@DubboService
public class MovementApiImpl implements MovementApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //评论数据存储在Redis中key的前缀
    private static final String COMMENT_REDIS_KEY_PREFIX = "QUANZI_COMMENT_";

    //用户是否点赞的前缀
    private static final String COMMENT_USER_LIKE_REDIS_KEY_PREFIX = "USER_LIKE_";

    //用户是否喜欢的前缀
    private static final String COMMENT_USER_LOVE_REDIS_KEY_PREFIX = "USER_LOVE_";

    /**
     * 1、查询用户动态
     *
     * @param userIds
     * @return
     */
    @Override
    public List<Movement> findByUserIds(List<Object> userIds,Integer page, Integer pagesize) {
        Pageable pageable= PageRequest.of(page-1,pagesize, Sort.by(Sort.Order.desc("created")));
        Query query=Query.query(Criteria.where("userId").in(userIds)).with(pageable);
        return mongoTemplate.find(query,Movement.class);
    }

    @Override
    public List<Movement> findByPids(List<Long> redisPids,Integer page, Integer pagesize) {
        Pageable pageable= PageRequest.of(page-1,pagesize, Sort.by(Sort.Order.desc("created")));
        Query query=Query.query(Criteria.where("pid").in(redisPids)).with(pageable);
        return mongoTemplate.find(query,Movement.class);
    }
    //发布动态
    @Override
    public void save(Movement movement) {
        mongoTemplate.save(movement);
    }


    /**
     * 点赞
     * @param movementId
     */
    @Override
    public void like(String movementId,Long id) {
        //1、redisKey:QUANZI_COMMENT_movementId
        String redisKey=COMMENT_REDIS_KEY_PREFIX+movementId;
        //2、hashKey: USER_LIKE_id   1
        String hashKey=COMMENT_USER_LIKE_REDIS_KEY_PREFIX+id;
        redisTemplate.opsForHash().put(redisKey,hashKey,"1");
        //3、保存点赞操作
        //保存的方法:参数-->动态id（评论id,视频id）  登录者id  评论内容  评论类型
        saveComment(movementId,id,null,CommentType.LIKE);
        //点赞数+1
        redisTemplate.opsForHash().increment(redisKey,CommentType.LIKE.toString(),1);
    }

    /**
     * 保存评论的操作：点赞  喜欢  评价  通用的方法
     * @param movementId
     * @param id
     * @param content
     * @param type
     */
    private void saveComment(String movementId, Long id, String content, CommentType type) {
        Comment comment=new Comment();
        //通过movementId查询当前动态的发布者
        Movement movement = mongoTemplate.findById(new ObjectId(movementId), Movement.class);
        //对获取的movement对象进行判断
        if(ObjectUtil.isNotNull(movement)){
            //动态点赞
            comment.setPublishId(new ObjectId(movementId));
            comment.setPublishUserId(movement.getUserId());
        }else {
            Comment cmt = mongoTemplate.findById(new ObjectId(movementId), Comment.class);
            if (ObjectUtil.isNotNull(cmt)){
                //评论点赞
                comment.setPublishId(cmt.getId());
                comment.setPublishUserId(cmt.getUserId());
            }else {
                //在当前项目中，点赞的操作有三部分：动态点赞，评论点赞，小视频点赞，所以这种情况即为小视频点赞行为处理
                Video video = mongoTemplate.findById(movementId, Video.class);
                comment.setPublishId(video.getId());
                comment.setPublishUserId(video.getUserId());
                if (ObjectUtil.isNull(video)){
                    throw new ConsumerException("非法操作");
                }
            }
        }
        comment.setUserId(id);
        comment.setContent(content);
        comment.setCommentType(type.getType());
        comment.setCreated(System.currentTimeMillis());
        mongoTemplate.save(comment);
    }

    /**
     * 点赞数
     * @param movementId
     */
    @Override
    public Long likeCount(String movementId) {
        //1、redisKey:QUANZI_COMMENT_movementId
        String redisKey=COMMENT_REDIS_KEY_PREFIX+movementId;
        //2、hashKey : LIKE
        String hashKey=CommentType.LIKE.toString();
        //3、从redis中获取点赞数
        Object redisCount = redisTemplate.opsForHash().get(redisKey, hashKey);//null
        if (ObjectUtil.isNull(redisCount)){//如果为空，则需要从数据库中获取
            Query query=Query.query(Criteria.where("publishId").is(new ObjectId(movementId))
                                        .and("commentType").is(CommentType.LIKE.getType()));
            redisCount = mongoTemplate.count(query, Comment.class);
            //将数据写会redis
            redisTemplate.opsForHash().put(redisKey,hashKey,Convert.toStr(redisCount));
        }
        return Convert.toLong(redisCount);
    }

    /**
     * 是否点赞
     *
     * @param movementId
     * @param id
     * @return
     */
    @Override
    public Boolean isLike(String movementId, Long id) {
        //1、redisKey:QUANZI_COMMENT_movementId
        String redisKey=COMMENT_REDIS_KEY_PREFIX+movementId;
        //2、hashKey: USER_LIKE_id   1
        String hashKey=COMMENT_USER_LIKE_REDIS_KEY_PREFIX+id;
        //3、从redis中获取数据
        Object data = redisTemplate.opsForHash().get(redisKey, hashKey);
        //4、如果data数据存在，则直接返回
        if(ObjectUtil.isNotEmpty(data)){
            return StrUtil.equals(Convert.toStr(data),"1");//如果是1则返回true,不是1则返回false
        }
        //5、如果data数据不存在，需要查询一次数据库中是否有点赞行为
        Query query=Query.query(Criteria.where("publishId").is(new ObjectId(movementId))
                                        .and("commentType").is(CommentType.LIKE.getType())
                                        .and("userId").is(id));
        long count = mongoTemplate.count(query, Comment.class);
        //6、如果没有，则直接返回fale，如果有则保存redis并返回true
        if(count==0){
            return false;
        }
        redisTemplate.opsForHash().put(redisKey, hashKey,"1");
        return true;
    }

    /**
     * 5、取消点赞操作
     *
     * @param movementId 当前点赞的动态id
     * @param id
     * @return 点赞数
     */
    @Override
    public void disLike(String movementId, Long id) {
        //1、redisKey:QUANZI_COMMENT_movementId
        String redisKey=COMMENT_REDIS_KEY_PREFIX+movementId;
        //2、hashKey: USER_LIKE_id   1
        String hashKey=COMMENT_USER_LIKE_REDIS_KEY_PREFIX+id;
        //3、从redis中直接删除即可
        redisTemplate.opsForHash().delete(redisKey,hashKey);
        //4、移除mongo中点赞数据
        Query query=Query.query(Criteria.where("publishId").is(new ObjectId(movementId))
                .and("commentType").is(CommentType.LIKE.getType())
                .and("userId").is(id));
        mongoTemplate.remove(query,Comment.class);
        //4、更新点赞数
        redisTemplate.opsForHash().increment(redisKey,CommentType.LIKE.toString(),-1);
    }

    /**
     * 6、喜欢操作
     *
     * @param movementId 当前点赞的动态id
     * @param id
     * @return 点赞数
     */
    @Override
    public void love(String movementId, Long id) {
        //如果已喜欢直接返回
     /*   if(isLove(movementId,id)){
            return;
        }*/


        //1、redisKey:QUANZI_COMMENT_movementId
        String redisKey=COMMENT_REDIS_KEY_PREFIX+movementId;
        //2、hashKey: USER_LIKE_id   1
        String hashKey=COMMENT_USER_LOVE_REDIS_KEY_PREFIX+id;
        redisTemplate.opsForHash().put(redisKey,hashKey,"1");
        //3、保存点赞操作
        //保存的方法:参数-->动态id（评论id,视频id）  登录者id  评论内容  评论类型
        saveComment(movementId,id,null,CommentType.LOVE);
        //点赞数+1
        redisTemplate.opsForHash().increment(redisKey,CommentType.LOVE.toString(),1);
    }

    /**
     * 7、取消喜欢操作
     *
     * @param movementId 当前点赞的动态id
     * @param id
     * @return 点赞数
     */
    @Override
    public void unLove(String movementId, Long id) {
        //如果已喜欢直接返回
     /*   if(!isLove(movementId,id)){
            return;
        }*/

        //1、redisKey:QUANZI_COMMENT_movementId
        String redisKey=COMMENT_REDIS_KEY_PREFIX+movementId;
        //2、hashKey: USER_LIKE_id   1
        String hashKey=COMMENT_USER_LOVE_REDIS_KEY_PREFIX+id;
        //3、从redis中直接删除即可
        redisTemplate.opsForHash().delete(redisKey,hashKey);
        //4、移除mongo中点赞数据
        Query query=Query.query(Criteria.where("publishId").is(new ObjectId(movementId))
                .and("commentType").is(CommentType.LOVE.getType())
                .and("userId").is(id));
        mongoTemplate.remove(query,Comment.class);
        //4、更新点赞数
        redisTemplate.opsForHash().increment(redisKey,CommentType.LOVE.toString(),-1);
    }

    /**
     * 喜欢数
     *
     * @param movementId
     * @return
     */
    @Override
    public Long loveCount(String movementId) {
        //1、redisKey:QUANZI_COMMENT_movementId
        String redisKey=COMMENT_REDIS_KEY_PREFIX+movementId;
        //2、hashKey : LIKE
        String hashKey=CommentType.LOVE.toString();
        //3、从redis中获取点赞数
        Object redisCount = redisTemplate.opsForHash().get(redisKey, hashKey);//null
        if (ObjectUtil.isNull(redisCount)){//如果为空，则需要从数据库中获取
            Query query=Query.query(Criteria.where("publishId").is(new ObjectId(movementId))
                    .and("commentType").is(CommentType.LOVE.getType()));
            redisCount = mongoTemplate.count(query, Comment.class);
            //将数据写会redis
            redisTemplate.opsForHash().put(redisKey,hashKey,Convert.toStr(redisCount));
        }
        return Convert.toLong(redisCount);
    }

    /**
     * 是否喜欢
     *
     * @param movementId
     * @param id
     * @return
     */
    @Override
    public Boolean isLove(String movementId, Long id) {
        //1、redisKey:QUANZI_COMMENT_movementId
        String redisKey=COMMENT_REDIS_KEY_PREFIX+movementId;
        //2、hashKey: USER_LIKE_id   1
        String hashKey=COMMENT_USER_LOVE_REDIS_KEY_PREFIX+id;
        //3、从redis中获取数据
        Object data = redisTemplate.opsForHash().get(redisKey, hashKey);
        //4、如果data数据存在，则直接返回
        if(ObjectUtil.isNotEmpty(data)){
            return StrUtil.equals(Convert.toStr(data),"1");//如果是1则返回true,不是1则返回false
        }
        //5、如果data数据不存在，需要查询一次数据库中是否有点赞行为
        Query query=Query.query(Criteria.where("publishId").is(new ObjectId(movementId))
                .and("commentType").is(CommentType.LOVE.getType())
                .and("userId").is(id));
        long count = mongoTemplate.count(query, Comment.class);
        //6、如果没有，则直接返回fale，如果有则保存redis并返回true
        if(count==0){
            return false;
        }
        redisTemplate.opsForHash().put(redisKey, hashKey,"1");
        return true;
    }

    /**
     * 评论数
     *
     * @param movementId
     * @return
     */
    @Override
    public Long commentCount(String movementId) {
        //1、redisKey:QUANZI_COMMENT_movementId
        String redisKey=COMMENT_REDIS_KEY_PREFIX+movementId;
        //2、hashKey : LIKE
        String hashKey=CommentType.COMMENT.toString();
        //3、从redis中获取点赞数
        Object redisCount = redisTemplate.opsForHash().get(redisKey, hashKey);//null
        if (ObjectUtil.isEmpty(redisCount) || Convert.toInt(redisCount)==0){//如果为空，则需要从数据库中获取
            Query query=Query.query(Criteria.where("publishId").is(new ObjectId(movementId))
                    .and("commentType").is(Convert.toStr(CommentType.COMMENT.getType())));
                    //.and("commentType").is(CommentType.COMMENT.getType()));
            redisCount = mongoTemplate.count(query, Comment.class);
            //将数据写会redis
            redisTemplate.opsForHash().put(redisKey,hashKey,Convert.toStr(redisCount));
        }
        return Convert.toLong(redisCount);
    }

    /**
     * 8、根据动态id查询动态信息
     *
     * @param movementId
     */
    @Override
    public Movement findOneMovement(String movementId) {
        return  mongoTemplate.findById(new ObjectId(movementId),Movement.class);
    }


    /**
     * 发表评论
     * @param commentId
     * @param content
     * @param id
     */
    @Override
    public void comment(String commentId,String content,Long id) {
        //执行保存评论
        saveComment(commentId,id,content, CommentType.COMMENT);
        //更新redis
        String redisKey=COMMENT_REDIS_KEY_PREFIX+commentId;
        redisTemplate.opsForHash().increment(redisKey,CommentType.COMMENT.toString(),1);
    }

    /**
     * 当前佳人动态
     * @return
     */
    @Override
    public List<Movement> findAllByUserId(Long userId, Integer page, Integer pagesize) {
        // 设置分页和排序
        Pageable pageable = PageRequest.of(page - 1,pagesize,Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("userId").is(userId)).with(pageable);
        return mongoTemplate.find(query,Movement.class);
    }
}
