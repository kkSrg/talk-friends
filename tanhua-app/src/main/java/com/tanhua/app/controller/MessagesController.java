package com.tanhua.app.controller;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.extension.api.R;
import com.tanhua.app.service.MessagesService;
import com.tanhua.db.pojo.Announcement;
import com.tanhua.db.vo.AnnouncementVo;
import com.tanhua.db.vo.MessagesVo;
import com.tanhua.db.vo.UserInfoVo;
import com.tanhua.mongo.PageResult;
import com.tanhua.mongo.vo.ContactVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("messages")
public class MessagesController {

    @Autowired
    private MessagesService messagesService;

    /**
     * 根据环信ID查询用户详细信息
     */
    @GetMapping("userinfo")
    public ResponseEntity userinfo(String huanxinId) {
        UserInfoVo vo = messagesService.findUserInfoByHxId(huanxinId);
        return ResponseEntity.ok(vo);
    }

    /**
     * 添加好友
     */
    @PostMapping("/contacts")
    public ResponseEntity contacts(@RequestBody Map map) {
        Long friendId = Convert.toLong(map.get("userId"));
        messagesService.contacts(friendId);
        return ResponseEntity.ok(null);
    }

    /**
     * 好友列表
     */
    @GetMapping("/contacts")
    public ResponseEntity<PageResult<ContactVo>> allFriends(@RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                                            @RequestParam(value = "pageszie",required = false,defaultValue = "10")Integer pagesize,
                                                            String keyword) {

        PageResult<ContactVo> result = messagesService.allFriends(keyword,page,pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 公告列表
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("announcements")
    public ResponseEntity<PageResult<AnnouncementVo>> announcements(@RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                                                    @RequestParam(value = "pageszie",required = false,defaultValue = "10")Integer pagesize){
        PageResult<AnnouncementVo> result = messagesService.allAnnouncements(page,pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 喜欢列表
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("loves")
    public ResponseEntity<PageResult<MessagesVo>> lovesList(@RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                                                @RequestParam(value = "pageszie",required = false,defaultValue = "10")Integer pagesize){
        PageResult<MessagesVo> result = messagesService.allList(page,pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 点赞列表
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("likes")
    public ResponseEntity<PageResult<MessagesVo>> likesList(@RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                                            @RequestParam(value = "pageszie",required = false,defaultValue = "10")Integer pagesize){
        PageResult<MessagesVo> result = new PageResult<>();
        return ResponseEntity.ok(result);
    }

    /**
     * 评论列表
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("comments")
    public ResponseEntity<PageResult<MessagesVo>> commentsList(@RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                                            @RequestParam(value = "pageszie",required = false,defaultValue = "10")Integer pagesize){
        PageResult<MessagesVo> result = new PageResult<>();
        return ResponseEntity.ok(result);
    }
}