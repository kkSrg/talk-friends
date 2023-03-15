package com.tanhua.app.controller;

import com.tanhua.app.service.MovementService;
import com.tanhua.mongo.PageResult;
import com.tanhua.mongo.dto.MovementDto;
import com.tanhua.mongo.vo.MovementVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("movements")
public class MovementController {

    @Autowired
    private MovementService movementService;

    /**
     * 1、查询好友动态
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping
    public ResponseEntity<PageResult<MovementVo>> movements(@RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                                            @RequestParam(value = "pageszie",required = false,defaultValue = "10")Integer pagesize){

        PageResult<MovementVo> result=movementService.movements(page,pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 2、查询推荐动态
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("recommend")
    public ResponseEntity<PageResult<MovementVo>> recommend(@RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                                            @RequestParam(value = "pageszie",required = false,defaultValue = "10")Integer pagesize){

        PageResult<MovementVo> result=movementService.recommend(page,pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 3、发布动态
     * @param imageContent
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseEntity saveMovement(MultipartFile[] imageContent, MovementDto dto){
        movementService.saveMovement(imageContent,dto);
        return ResponseEntity.ok(null);
    }

    /**
     * 4、动态点赞操作
     * @param movementId  当前点赞的动态id
     * @return     点赞数
     */
    @GetMapping("{id}/like")
    public ResponseEntity<Long> like(@PathVariable("id") String movementId){
        Long count=movementService.like(movementId);
        return ResponseEntity.ok(count);
    }
    /**
     * 5、取消点赞操作
     * @param movementId  当前点赞的动态id
     * @return     点赞数
     */
    @GetMapping("{id}/dislike")
    public ResponseEntity<Long> disLike(@PathVariable("id") String movementId){
        Long count=movementService.disLike(movementId);
        return ResponseEntity.ok(count);
    }

    /**
     * 6、喜欢操作
     * @param movementId  当前点赞的动态id
     * @return     点赞数
     */
    @GetMapping("{id}/love")
    public ResponseEntity<Long> love(@PathVariable("id") String movementId){
        Long count=movementService.love(movementId);
        return ResponseEntity.ok(count);
    }
    /**
     * 7、取消喜欢操作
     * @param movementId  当前点赞的动态id
     * @return     点赞数
     */
    @GetMapping("{id}/unlove")
    public ResponseEntity<Long> unLove(@PathVariable("id") String movementId){
        Long count=movementService.unLove(movementId);
        return ResponseEntity.ok(count);
    }

    /**
     * 8、根据动态id查询动态信息
     * @param movementId
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<MovementVo> findOneMovement(@PathVariable("id") String movementId){
        MovementVo result=movementService.findOneMovement(movementId);
        return ResponseEntity.ok(result);
    }


    /**
     * TODO 9、谁看过我？
     */
    @GetMapping("visitors")
    public ResponseEntity visitors(){
        return ResponseEntity.ok(new ArrayList());
    }

    /**
     * 10. 当前佳人动态
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("all")
    public ResponseEntity<PageResult<MovementVo>> all(@RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                              @RequestParam(value = "pageszie",required = false,defaultValue = "10")Integer pagesize,
                              Long userId){
        PageResult<MovementVo> result = movementService.all(userId,page,pagesize);
        return ResponseEntity.ok(result);
    }
}
