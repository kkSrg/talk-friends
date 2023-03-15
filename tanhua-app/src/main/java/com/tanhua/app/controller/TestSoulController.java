package com.tanhua.app.controller;

import cn.hutool.core.convert.Convert;
import com.tanhua.app.service.TestSoulService;
import com.tanhua.db.dto.AnswersDto;
import com.tanhua.db.vo.ReportVo;
import com.tanhua.db.vo.TestSoulVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("testSoul")
public class TestSoulController {

    @Autowired
    private TestSoulService testSoulService;

    /**
     * 测灵魂-问卷列表
     * @return
     */
    @GetMapping()
    public ResponseEntity testSoul() {
        List<TestSoulVo> voList = testSoulService.getTestSoul();
        return ResponseEntity.ok(voList);
    }


    /**
     * 测灵魂-提交问卷
     * @param answers
     * @return
     */
    @PostMapping
    public ResponseEntity answerSoulTest(@RequestBody Map<String,List<AnswersDto>> answers){
        List<AnswersDto> answers1 = answers.get("answers");
        Long reportId = testSoulService.answerTestSoul(answers1);
        return ResponseEntity.ok(Convert.toStr(reportId));
    }

    /**
     * 测灵魂-查看结果
     * @param id
     * @return
     */
    @GetMapping("report/{id}")
    public ResponseEntity<ReportVo> report(@PathVariable String id){
        ReportVo vo = testSoulService.reportResult(Convert.toLong(id));
        return ResponseEntity.ok(vo);
    }
}
