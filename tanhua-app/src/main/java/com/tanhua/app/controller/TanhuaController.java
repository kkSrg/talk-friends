package com.tanhua.app.controller;

import com.tanhua.app.service.TanhuaService;
import com.tanhua.app.service.UserLocationService;
import com.tanhua.mongo.PageResult;
import com.tanhua.mongo.dto.RecommendationDto;
import com.tanhua.mongo.vo.NearUserVo;
import com.tanhua.mongo.vo.TodayBestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("tanhua")
public class TanhuaController {

    @Autowired
    private TanhuaService tanhuaService;

    @Autowired
    private UserLocationService userLocationService;

    /**
     * 1、今日佳人
     * @return
     */
    @GetMapping("todayBest")
    public ResponseEntity<TodayBestVo> todayBest(){
        TodayBestVo result=tanhuaService.todayBest();
        return ResponseEntity.ok(result);
    }

    /**
     * 2、佳人推荐列表
     * @return
     */
    @GetMapping("recommendation")
    public ResponseEntity<PageResult<TodayBestVo>> recommendation(@RequestParam(value = "page",required = false,defaultValue = "1") Integer page,
                                                                  @RequestParam(value = "pageszie",required = false,defaultValue = "10")Integer pagesize,
                                                                  RecommendationDto dto){
        PageResult<TodayBestVo> result=tanhuaService.recommendation(page,pagesize,dto);
        return ResponseEntity.ok(result);
    }

    /**
     * 3. 佳人信息
     * @param id : 当前被点击佳人的id
     * @return
     */
    @GetMapping("{id}/personalInfo")
    public ResponseEntity<TodayBestVo> personalInfo(@PathVariable Long id){
        TodayBestVo result = tanhuaService.personalInfo(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 4. 查询陌生人问题
     * @param userId
     * @return
     */
    @GetMapping("strangerQuestions")
    public ResponseEntity strangerQuestions(Long userId){
        String content = tanhuaService.strangerQuestions(userId);
        return ResponseEntity.ok(content);
    }

    /**
     * 5. 回复陌生人问题
     * @param map
     * @return
     */
    @PostMapping("strangerQuestions")
    public ResponseEntity replyQuestions(@RequestBody Map map){
        tanhuaService.replyQuestions(map);
        return ResponseEntity.ok(null);
    }

    /**
     *  6. 探花卡片查询(左滑右滑)
     * @return
     */
    @GetMapping("cards")
    public ResponseEntity<List<TodayBestVo>> cards(){
        List<TodayBestVo> result = tanhuaService.cards();
        return ResponseEntity.ok(result);
    }

    /**
     * 喜欢(右滑)
     */
    @GetMapping("{id}/love")
    public ResponseEntity<Void> likeUser(@PathVariable("id") Long likeUserId) {
        tanhuaService.likeUser(likeUserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 不喜欢(左滑)
     */
    @GetMapping("{id}/unlove")
    public ResponseEntity<Void> notLikeUser(@PathVariable("id") Long likeUserId) {
        tanhuaService.notLikeUser(likeUserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 7. 搜附近
     * @param gender
     * @param distance
     * @return
     */
    @GetMapping("search")
    public ResponseEntity<List<NearUserVo>> search(String gender,
                                                   @RequestParam(defaultValue = "2000") String distance){
        List<NearUserVo> result = userLocationService.search(gender,distance);
        return ResponseEntity.ok(result);
    }

}
