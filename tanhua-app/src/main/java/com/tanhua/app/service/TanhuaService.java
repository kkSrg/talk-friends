package com.tanhua.app.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.api.app.QuestionApi;
import com.tanhua.api.app.RecommendUserApi;
import com.tanhua.api.app.UserLikeApi;
import com.tanhua.api.app.VisitorsApi;
import com.tanhua.api.sso.UserInfoServiceApi;
import com.tanhua.api.sso.UserServiceApi;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.db.pojo.User;
import com.tanhua.db.pojo.UserInfo;
import com.tanhua.exception.ConsumerException;
import com.tanhua.mongo.PageResult;
import com.tanhua.mongo.dto.RecommendationDto;
import com.tanhua.mongo.pojo.RecommendUser;
import com.tanhua.mongo.pojo.Visitors;
import com.tanhua.mongo.vo.TodayBestVo;
import com.tanhua.utils.ThreadLocalUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TanhuaService {
    @DubboReference
    private UserServiceApi userServiceApi;
    @DubboReference
    private RecommendUserApi recommentUserApi;
    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;
    @Autowired
    private HuanXinTemplate huanXinTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MessagesService messagesService;
    @DubboReference
    private UserLikeApi userLikeApi;
    @DubboReference
    private VisitorsApi visitorsApi;

    //评论数据存储在Redis中key的前缀
    private static final String LIKE_KEY_PREFIX = "USER_LIKE_SET_";

    //用户是否点赞的前缀
    private static final String NOTLIKE_KEY_PREFIX = "USER_NOT_LIKE_SET_";


    /**
     * 1、查询今日佳人
     *   查询当前登陆者的所有佳人中缘分值最高的人为今日佳人
     * @return
     */
    public TodayBestVo todayBest() {
        //1、获取当前登录者id
        Long id = ThreadLocalUtil.getId();
        //2、根据id查询recommentUser
        RecommendUser recommentUser=recommentUserApi.findByToUserId(id);
        //3、获取userId
        Long userId = recommentUser.getUserId();
        //4、向mysql发送根据id查询个人详细信息
        UserInfo userInfo=userInfoServiceApi.findById(userId);
        TodayBestVo vo=new TodayBestVo();
        //5、值得copy  userIfo-->vo
        BeanUtils.copyProperties(userInfo,vo);
        //6、处理未实现copy的属性
        vo.setTags(StrUtil.split(userInfo.getTags(),","));
        vo.setFateValue(Convert.toInt(recommentUser.getScore()));
        return vo;
    }


    /**
     * 2、佳人推荐列表
     * @return
     */
    public PageResult<TodayBestVo> recommendation(Integer page, Integer pagesize, RecommendationDto dto) {
        //1、获取当前登录者id
        Long id = ThreadLocalUtil.getId();
        //2、查询当前登录者所有推荐的佳人
        List<RecommendUser> recommendUserList=recommentUserApi.findAllByToUserId(id,page,pagesize);
        //3、recommentUserList
        List<TodayBestVo> voList=recommendUserList.stream().map(recommendUser -> {
            //3、获取userId
            Long userId = recommendUser.getUserId();
            //4、向mysql发送根据id查询个人详细信息
            UserInfo userInfo=userInfoServiceApi.findById(userId);
            //UserInfo userInfo=userInfoServiceApi.findByIdAndCondation(userId,dto);
            TodayBestVo vo =null;
            if (ObjectUtil.isNotNull(userInfo)) {
                vo = new TodayBestVo();
                //5、值得copy  userIfo-->vo
                BeanUtils.copyProperties(userInfo, vo);
                //6、处理未实现copy的属性
                vo.setTags(StrUtil.split(userInfo.getTags(), ","));
                vo.setFateValue(Convert.toInt(recommendUser.getScore()));
            }
            return vo;
        }).collect(Collectors.toList());
        PageResult<TodayBestVo> result=new PageResult<>();
        result.setPage(page);
        result.setPagesize(pagesize);
        result.setItems(voList);
        return result;
    }

    /**
     * 3. 佳人信息
     * @param id : 当前被点击佳人的id
     */
    public TodayBestVo personalInfo(Long id) {
        // 1. 获取当前登录者id
        Long uid = ThreadLocalUtil.getId();
        // 2. 根据id查询个人信息
        UserInfo userInfo = userInfoServiceApi.findById(id);
        // 3. 根据id,uid查询缘分值
        Integer score = recommentUserApi.findScore(id,uid);

        // 构造访客数据,调用API保存到visitors表中
        Visitors visitors = new Visitors();
        visitors.setUserId(id);
        visitors.setVisitorUserId(uid);
        visitors.setFrom("首页");
        visitors.setDate(System.currentTimeMillis());
        visitors.setVisitDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        visitors.setScore(Convert.toDouble(score));
        visitorsApi.save(visitors);


        TodayBestVo vo = new TodayBestVo();
        BeanUtil.copyProperties(userInfo,vo);
        vo.setTags(StrUtil.split(userInfo.getTags(),","));
        vo.setFateValue(score);
        return vo;
    }


    @DubboReference
    private QuestionApi questionApi;
    /**
     * 4. 查询陌生人问题
     * @param userId
     */
    public String strangerQuestions(Long userId) {
        String txt = questionApi.findTxtByUserId(userId);
        // 如果当前用户没有设置问题
        if (StrUtil.isBlank(txt)){
            txt = "你想和我成为好友吗?";
        }
        return txt;
    }

    /**
     * 5. 回复陌生人问题
     * @param map
     */
    public void replyQuestions(Map map) {
        Long userId = Convert.toLong(map.get("userId"));
        String reply = Convert.toStr(map.get("reply"));
        String question = strangerQuestions(userId);
        // 1. 构造消息数据
        Long id = ThreadLocalUtil.getId();
        User user = userServiceApi.findById(id);  //当前用户的环信
        User toUser = userServiceApi.findById(userId);  //接收方的环信
        UserInfo userInfo = userInfoServiceApi.findById(id);
        Map questionMap = new HashMap();
        questionMap.put("userId",id);
        questionMap.put("huanXinId",user.getHxUser());
        questionMap.put("nickname",userInfo.getNickname());
        questionMap.put("strangerQuestion",question);
        questionMap.put("reply",reply);

        String message = JSON.toJSONString(questionMap);
        // 2. 调用template对象,发送消息
        Boolean sendMsg = huanXinTemplate.sendMsg(toUser.getHxUser(), message);  //  1. 接收方的id  2. 消息
        if (!sendMsg){
            throw new ConsumerException("发送消息失败");
        }
    }


    //指定默认数据
    @Value("${tanhua.default.recommend.users}")
    private String recommendUser;
    /**
     *  6. 探花卡片查询(左滑右滑)
     */
    public List<TodayBestVo> cards() {
        Long id = ThreadLocalUtil.getId();
        //1、调用推荐API查询数据列表（排除喜欢/不喜欢的用户，数量限制）
        List<RecommendUser> users = recommentUserApi.queryCardsList(id,10);
        //2、判断数据是否存在，如果不存在，构造默认数据 1,2,3
        if(CollUtil.isEmpty(users)) {
            users = new ArrayList<>();
            String[] userIdS = recommendUser.split(",");
            for (String userId : userIdS) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Convert.toLong(userId));
                recommendUser.setToUserId(id);
                recommendUser.setScore(RandomUtil.randomDouble(60, 90));
                users.add(recommendUser);
            }
        }
        //3. 获取推荐用户的id
        List<Long> userIds = CollUtil.getFieldValues(users, "userId", Long.class);
        //4. 根据用户id查询用户信息
        List<TodayBestVo> list = userIds.stream().map(uid -> {
            UserInfo userInfo = userInfoServiceApi.findById(uid);
            TodayBestVo vo = new TodayBestVo();
            BeanUtil.copyProperties(userInfo, vo);
            vo.setTags(StrUtil.split(userInfo.getTags(), ","));
            return vo;
        }).collect(Collectors.toList());


        return list;
    }

    /**
     * 喜欢
     */
    public void likeUser(Long likeUserId) {
        Long id = ThreadLocalUtil.getId();
        //1、调用API，保存喜欢数据(保存到MongoDB中)
        Boolean save = userLikeApi.saveOrUpdate(id,likeUserId,true);
        if(!save) {
            //失败
            throw new ConsumerException("操作失败");
        }
        //2、操作redis，写入喜欢的数据，删除不喜欢的数据 (喜欢的集合，不喜欢的集合)
        redisTemplate.opsForSet().remove(LIKE_KEY_PREFIX+id,likeUserId.toString());
        redisTemplate.opsForSet().add(NOTLIKE_KEY_PREFIX+id,likeUserId.toString());
        //3、判断是否双向喜欢
        if(isLike(likeUserId,id)) {
            //4、添加好友
            messagesService.contacts(likeUserId);
        }
    }

    /**
     * 不喜欢
     */
    public void notLikeUser(Long likeUserId) {
        Long id = ThreadLocalUtil.getId();
        //1、调用API，保存不喜欢数据(保存到MongoDB中)
        Boolean save = userLikeApi.saveOrUpdate(id,likeUserId,false);
        if(!save) {
            //失败
            throw new ConsumerException("操作失败");
        }
        //2、操作redis，写入喜欢的数据，删除不喜欢的数据 (喜欢的集合，不喜欢的集合)
        redisTemplate.opsForSet().remove(LIKE_KEY_PREFIX+id,likeUserId.toString());
        redisTemplate.opsForSet().add(NOTLIKE_KEY_PREFIX+id,likeUserId.toString());
        //3. 判断是否双向喜欢，删除好友
        if(!isLike(likeUserId,id)) {
            messagesService.unContacts(likeUserId);
        }
    }

    //判断是否双向喜欢
    public Boolean isLike(Long userId, Long likeUserId) {
        String key = LIKE_KEY_PREFIX + userId;
        return redisTemplate.opsForSet().isMember(key, likeUserId.toString());
    }
}
