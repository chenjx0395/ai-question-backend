package com.cjx.aiquestion.utils;

import com.alibaba.fastjson.JSON;
import com.cjx.aiquestion.manager.ZhiPuAiManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.xml.ws.ResponseWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.cjx.aiquestion.constant.Prompt.SYS_SET_QUESTION_PROMPT;
import static com.zhipu.oapi.demo.V4OkHttpClientTest.mapStreamToAccumulator;

/**
 * ai功能测试类
 *
 * @author cjx
 * @date 2024-07-30
 */
@SpringBootTest
public class AIGeneratedTest {

    // 假设这是 requestIdTemplate 的定义，它是一个带有占位符的字符串模板
    private static final String requestIdTemplate = "req-%d";

    // 这是 mapper 的实例化，用于之后的JSON序列化
    private static final ObjectMapper mapper = new ObjectMapper();

    @Resource
    private ClientV4 client;


    @Test
    public void test() {
        /**
         * 同步调用
         */
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "作为一名营销专家，请为智谱开放平台创作一个吸引人的slogan");
        messages.add(chatMessage);
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        try {
            System.out.println("model output:" + mapper.writeValueAsString(invokeModelApiResp));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Resource
    private ZhiPuAiManager zhiPuAiManager;

    @Test
    public void test2() {
        String userMessage = "小学数学测验，\n" +
                "【【【小学三年级的数学题】】】，\n" +
                "得分类，\n" +
                "10，\n" +
                "3\n";
        System.out.println(zhiPuAiManager.doStabilizeSysInvokeChatRequest(SYS_SET_QUESTION_PROMPT, userMessage));
    }

    @Test
    public void test3() throws InterruptedException {
        String userMessage = "小学数学测验，\n" +
                "【【【小学三年级的数学题】】】，\n" +
                "得分类，\n" +
                "5，\n" +
                "2\n";
        ModelApiResponse sseModelApiResp = zhiPuAiManager.doSysStreamStableChatRequest(SYS_SET_QUESTION_PROMPT, userMessage);
        sseModelApiResp.getFlowable()
                .observeOn(Schedulers.io()) // 或者选择其他合适的调度器
                .map(chunk -> chunk.getChoices().get(0).getDelta().getContent())
                .doOnNext(System.out::println)
                .subscribe();

        Thread.sleep(30000L);


    }


}
