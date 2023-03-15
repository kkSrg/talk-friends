package com.tanhua.app.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.tanhua.api.app.AnnouncementApi;
import com.tanhua.api.app.FriendApi;
import com.tanhua.api.app.UserLikeApi;
import com.tanhua.api.sso.UserInfoServiceApi;
import com.tanhua.api.sso.UserServiceApi;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.db.pojo.Announcement;
import com.tanhua.db.pojo.User;
import com.tanhua.db.pojo.UserInfo;
import com.tanhua.db.vo.AnnouncementVo;
import com.tanhua.db.vo.MessagesVo;
import com.tanhua.db.vo.UserInfoVo;
import com.tanhua.exception.ConsumerException;
import com.tanhua.mongo.PageResult;
import com.tanhua.mongo.pojo.Friend;
import com.tanhua.mongo.vo.ContactVo;
import com.tanhua.utils.ThreadLocalUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessagesService {

    @DubboReference
    private UserServiceApi userServiceApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private FriendApi friendApi;



    /**
     * 根据环信id查询用户详情
     */
    public UserInfoVo findUserInfoByHxId(String huanxinId) {
        //1、根据环信id查询用户
        User user = userServiceApi.findByHxUser(huanxinId);
        //2、根据用户id查询用户详情
        UserInfo userInfo = userInfoServiceApi.findById(user.getId());
        UserInfoVo vo = new UserInfoVo();

        BeanUtils.copyProperties(userInfo,vo); //copy同名同类型的属性
        vo.setAge(Convert.toStr(userInfo.getAge()));
        return vo;
    }

    /**
     * 添加好友
     */
    public void contacts(Long friendId) {
        Long id = ThreadLocalUtil.getId();

        //1、将好友关系注册到环信
        Boolean flag = huanXinTemplate.addContact("hx" + id, "hx" + friendId);
        if (!flag){
            throw new ConsumerException("添加好友失败 ! !");
        }
        //2、如果注册成功，记录好友关系到mongodb
        friendApi.addContact(id,friendId);
    }

    /**
     * 删除好友
     */
    public void unContacts(Long friendId) {
        Long id = ThreadLocalUtil.getId();

        //1、环信删除好友关系
        Boolean flag = huanXinTemplate.deleteContact("hx" + id, "hx" + friendId);
        if (!flag){
            throw new ConsumerException("删除好友失败 ! !");
        }
        //2、如果删除成功，记录好友关系到mongodb
        friendApi.deleteContact(id,friendId);
    }

    /**
     * 好友列表
     */
    public PageResult<ContactVo> allFriends(String keyword, Integer page, Integer pagesize) {
        //1、获取登陆者id
        Long id = ThreadLocalUtil.getId();
        //2、根据id查询所有好友
        List<Friend> friendList = friendApi.findByFriendId(id);
        PageResult<ContactVo> result=new PageResult<>();

        result.setCounts(friendList.size());
        result.setPages(100);
        result.setPage(page);
        result.setPagesize(pagesize);
        if(CollUtil.isEmpty(friendList)){
            return result;
        }
        //3、从friendList获取好友id
        List<Long> friendIds = CollUtil.getFieldValues(friendList, "friendId",Long.class);
        //4、带有分页及关键字模糊查询
        List<UserInfo> userInfoList = userInfoServiceApi.findByIdsPageAndKw(friendIds, keyword, page, pagesize);
        List<ContactVo> contactVoList=userInfoList.stream().map(userInfo -> {
            ContactVo vo=new ContactVo();
            BeanUtil.copyProperties(userInfo,vo);
            vo.setUserId("hx"+userInfo.getId());
            return vo;
        }).collect(Collectors.toList());

        //4、根据friends查询对应的好友信息
      /*  List<ContactVo> list=new ArrayList<>();
        for (Long friendId : friendIds) {
            UserInfo userInfo = userInfoServiceApi.findById(friendId);
            ContactVo vo=new ContactVo();
            BeanUtil.copyProperties(userInfo,vo);
            vo.setUserId("hx"+userInfo.getId());//环信id
            list.add(vo);
        }*/
      /*  List<ContactVo> list=friendIds.stream().map(fid->{
            UserInfo userInfo = userInfoServiceApi.findById(fid);
            ContactVo vo=new ContactVo();
            BeanUtil.copyProperties(userInfo,vo);
            vo.setUserId("hx"+userInfo.getId());//环信id
            return vo;
        }).collect(Collectors.toList());*/

        result.setItems(contactVoList);
        return result;
    }

    @DubboReference
    private AnnouncementApi announcementApi;
    /**
     * 公告列表
     * @param page
     * @param pagesize
     */
    public PageResult<AnnouncementVo> allAnnouncements(Integer page, Integer pagesize) {
        PageResult<AnnouncementVo> listPageResult = new PageResult<>();
        listPageResult.setPage(page);
        listPageResult.setPagesize(pagesize);
        listPageResult.setPages(100);
        listPageResult.setCounts(100);

        List<Announcement> list = announcementApi.findAll(page,pagesize);
        List<AnnouncementVo> announcementVoList = list.stream().map(announcement -> {
            AnnouncementVo vo = new AnnouncementVo();
            BeanUtil.copyProperties(announcement,vo);
            vo.setId(Convert.toStr(announcement.getId()));
            vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(announcement.getCreated()));
            return vo;
        }).collect(Collectors.toList());

        listPageResult.setItems(announcementVoList);
        return listPageResult;
    }


    @DubboReference
    private UserLikeApi userLikeApi;



    public PageResult<MessagesVo> allList(Integer page, Integer pagesize) {
        Long id = ThreadLocalUtil.getId();
        PageResult<MessagesVo> listPageResult = new PageResult<>();
        listPageResult.setPage(page);
        listPageResult.setPagesize(pagesize);
        listPageResult.setPages(100);
        listPageResult.setCounts(100);
        //喜欢loves

        return listPageResult;
    }
}