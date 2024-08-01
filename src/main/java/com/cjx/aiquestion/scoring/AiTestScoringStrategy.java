package com.cjx.aiquestion.scoring;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cjx.aiquestion.manager.ZhiPuAiManager;
import com.cjx.aiquestion.model.dto.question.QuestionContentDTO;
import com.cjx.aiquestion.model.dto.scoringresult.QuestionAnswerDTO;
import com.cjx.aiquestion.model.entity.App;
import com.cjx.aiquestion.model.entity.Question;
import com.cjx.aiquestion.model.entity.UserAnswer;
import com.cjx.aiquestion.model.vo.QuestionVO;
import com.cjx.aiquestion.service.QuestionService;
import com.cjx.aiquestion.service.ScoringResultService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.cjx.aiquestion.constant.Prompt.SYS_ANSWER_PROMPT;

/**
 * 用于Ai测评类应用评分
 *
 * @author cjx
 * @date 2024-07-21
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AiTestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;
    @Resource
    private ZhiPuAiManager aiManager;
    @Resource
    private RedissonClient redissonClient;

    // 分布式锁的 key
    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";

    private final Cache<String, String> answerCacheMap =
            Caffeine.newBuilder().initialCapacity(1024)
                    // 缓存一天移除
                    .expireAfterAccess(1L, TimeUnit.DAYS)
                    .build();


    private String getAiTestScoringUserMessage(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();

        String jsonStr = JSONUtil.toJsonStr(choices);
        String cacheKey = getCacheKey(appId, jsonStr);
        String answerJson = answerCacheMap.getIfPresent(cacheKey);
        // 如果有缓存，直接返回
        if (StrUtil.isNotBlank(answerJson)) {
            // 构造返回值，填充答案对象的属性
            UserAnswer userAnswer = JSONUtil.toBean(answerJson, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonStr);
            return userAnswer;
        }


        // 定义锁
        RLock lock = redissonClient.getLock(AI_ANSWER_LOCK + cacheKey);
        try {
            // 竞争锁
            boolean res = lock.tryLock(3, 15, TimeUnit.SECONDS);
            // 没抢到锁，强行返回
            if (!res) {
                return null;
            }
            // 抢到锁了，执行后续业务逻辑
            // 1. 根据 id 查询到题目
            Question question = questionService.getOne(
                    Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
            );
            QuestionVO questionVO = QuestionVO.objToVo(question);
            List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
            // 2. 调用 AI 获取结果
            // 封装 Prompt
            String userMessage = getAiTestScoringUserMessage(app, questionContent, choices);
            // AI 生成
            String result = aiManager.doStabilizeSysInvokeChatRequest(SYS_ANSWER_PROMPT, userMessage);
            // 结果处理
            int start = result.indexOf("{");
            int end = result.lastIndexOf("}");
            String json = result.substring(start, end + 1);

            // 缓存结果
            answerCacheMap.put(cacheKey, json);

            // 3. 构造返回值，填充答案对象的属性
            UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(JSONUtil.toJsonStr(choices));
            return userAnswer;
        } finally {
            if (lock != null && lock.isLocked()) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    private static String getCacheKey(Long appId, String jsonStr) {
        return appId + ":" + DigestUtil.md5Hex(jsonStr);
    }
}

