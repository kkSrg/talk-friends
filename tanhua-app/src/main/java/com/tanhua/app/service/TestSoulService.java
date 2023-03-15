package com.tanhua.app.service;

import cn.hutool.core.convert.Convert;
import com.tanhua.api.app.*;
import com.tanhua.api.sso.UserInfoServiceApi;
import com.tanhua.db.dto.AnswersDto;
import com.tanhua.db.pojo.*;
import com.tanhua.db.vo.OptionsVo;
import com.tanhua.db.vo.ReportVo;
import com.tanhua.db.vo.SoulQuestionVo;
import com.tanhua.db.vo.TestSoulVo;
import com.tanhua.enums.ConclusionType;
import com.tanhua.utils.ThreadLocalUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestSoulService {

    @DubboReference
    private QuestionnaireApi questionnaireApi;

    @DubboReference
    private SoulQuestionApi soulQuestionApi;

    @DubboReference
    private OptionsApi optionsApi;

    @DubboReference
    private UserTestApi userTestApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @DubboReference
    private ConclusionApi conclusionApi;



    //测灵魂-问卷列表
    public List<TestSoulVo> getTestSoul() {
        //先查Questionnaire表,得到不同等级的测试题
        List<Questionnaire> list = questionnaireApi.getAll();
        //遍历封装TestSoulVo
        List<TestSoulVo> voList = list.stream().map(questionnaire -> {
            TestSoulVo vo = new TestSoulVo();
            BeanUtils.copyProperties(questionnaire, vo);
            vo.setId(Convert.toStr(questionnaire.getId()));
            vo.setStar(Convert.toInt(questionnaire.getStar()));
            //根据问卷编号查出对应的问题id
            Long qId = questionnaire.getId();
            //查询soul_question表
            List<SoulQuestion> soulQuestions = soulQuestionApi.getByQId(qId);
            //封装SoulQuestionVo
            List<SoulQuestionVo> soulQuestionVoList = soulQuestions.stream().map(soulQuestion -> {
                SoulQuestionVo soulQuestionVo = new SoulQuestionVo();
                BeanUtils.copyProperties(soulQuestion, soulQuestionVo);
                soulQuestionVo.setId(Convert.toStr(soulQuestion.getId()));

                Long sqId = soulQuestion.getId();
                //查选项表
                List<Options> options = optionsApi.getBySqId(sqId);
                List<OptionsVo> optionsVos = options.stream().map(option -> {
                    OptionsVo optionsVo = new OptionsVo();
                    optionsVo.setId(Convert.toStr(option.getId()));
                    optionsVo.setOption(option.getOptions());//!!!注意option是关键字
                    return optionsVo;
                }).collect(Collectors.toList());
                soulQuestionVo.setOptions(optionsVos);
                return soulQuestionVo;
            }).collect(Collectors.toList());
            vo.setQuestions(soulQuestionVoList);
            vo.setIsLock(0);
            vo.setReportId(Convert.toStr(userTestApi.findByUserId(ThreadLocalUtil.getId())));

            return vo;
        }).collect(Collectors.toList());

        return voList;
    }

    /**
     * 测灵魂-提交问卷
     *
     * @param answers
     */
    public Long answerTestSoul(List<AnswersDto> answers) {
        Long uid = ThreadLocalUtil.getId();
        Integer score = 0;
        for (AnswersDto dto : answers) {
            String questionId = dto.getQuestionId();
            String optionId = dto.getOptionId();
            score += optionsApi.findScore(questionId, optionId);
        }
        UserTest userTest = new UserTest();
        userTest.setUserId(uid);
        userTest.setCreated(new Date());
        userTest.setScore(score);
        userTest.setQid(soulQuestionApi.getQId(answers.get(2)));
        if (score < 21) {
            userTest.setConclusionId(ConclusionType.OWL.getType());
        } else if (score >= 21 && score <= 40) {
            userTest.setConclusionId(ConclusionType.RABBIT.getType());
        } else if (score >= 41 && score <= 55) {
            userTest.setConclusionId(ConclusionType.FOX.getType());
        } else {
            userTest.setConclusionId(ConclusionType.LION.getType());
        }

        Long reportId = userTestApi.save(userTest);
        return reportId;
    }


    /**
     * 测灵魂-查看结果
     *
     * @param id
     */
    public ReportVo reportResult(Long id) {
        ReportVo vo = new ReportVo();
        Long uid = ThreadLocalUtil.getId();

        UserTest userTest = userTestApi.findById(id);
        Integer conclusionId = userTest.getConclusionId();
        List<Long> userIds = userTestApi.getByConclusionId(conclusionId, 1L);
        List<Map<String, Object>> similarYou = userIds.stream().map(userId -> {
            Map<String, Object> map = new HashMap<>();
            UserInfo userInfo = userInfoServiceApi.slectById(userId);
            map.put("id", userId);
            map.put("avatar", userInfo.getAvatar());
            return map;
        }).collect(Collectors.toList());

        vo.setSimilarYou(similarYou != null ? similarYou : new ArrayList());
        Conclusion conclusion = conclusionApi.find(conclusionId);
        vo.setConclusion(conclusion.getConclusion());
        vo.setCover(conclusion.getCover());
        List<Map<String, String>> dimensions = new ArrayList<>();

        Map<String, String> map1 = new HashMap<>();
        map1.put("key", "外向");
        map1.put("value", "80%");
        dimensions.add(map1);

        Map<String, String> map2 = new HashMap<>();
        map2.put("key", "判断");
        map2.put("value", "70%");
        dimensions.add(map2);

        Map<String, String> map3 = new HashMap<>();
        map3.put("key", "抽象");
        map3.put("value", "90%");
        dimensions.add(map3);

        Map<String, String> map4 = new HashMap<>();
        map4.put("key", "理性");
        map4.put("value", "60%");
        dimensions.add(map4);

        vo.setDimensions(dimensions);
        return vo;
    }

}
