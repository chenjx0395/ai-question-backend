package com.cjx.aiquestion.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 智谱AI的问答调用
 *
 * @author cjx
 * @date 2024-07-30
 */
@Component
public class ZhiPuAiManager {


    @Resource
    private ClientV4 client;

    // 稳定的随机数
    private static final float STABLE_TEMPERATURE = 0.05f;

    // 不稳定的随机数
    private static final float UNSTABLE_TEMPERATURE = 0.99f;


    /**
     * 稳定的流式调用
     * @param sysMessage
     * @param userMessage
     * @return
     */
    public ModelApiResponse doSysStreamStableChatRequest(String sysMessage
            , String userMessage
    ) {
        return doSysStreamChatRequest(sysMessage, userMessage, STABLE_TEMPERATURE);
    }

    /**
     * 流式调用
     *
     * @param sysMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public ModelApiResponse doSysStreamChatRequest(String sysMessage
            , String userMessage
            , Float temperature
    ) {

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), sysMessage));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), userMessage));

        return doBaseRequest(messages, Constants.ModelChatGLM4, Boolean.TRUE, Constants.invokeMethod, temperature);

    }


    /**
     * 稳定调用
     *
     * @param sysMessage
     * @param userMessage
     * @return
     */
    public String doStabilizeSysInvokeChatRequest(String sysMessage
            , String userMessage
    ) {
        return doSysInvokeChatRequest(sysMessage, userMessage, STABLE_TEMPERATURE);
    }

    /**
     * 不稳定调用
     *
     * @param sysMessage
     * @param userMessage
     * @return
     */
    public String doNoStabilizeSysInvokeChatRequest(String sysMessage
            , String userMessage
    ) {
        return doSysInvokeChatRequest(sysMessage, userMessage, UNSTABLE_TEMPERATURE);
    }


    /**
     * 同步调用
     *
     * @param sysMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public String doSysInvokeChatRequest(String sysMessage
            , String userMessage
            , Float temperature
    ) {



        return doSysChatRequest(sysMessage, userMessage, Boolean.FALSE, Constants.invokeMethod, temperature);
    }


    /**
     * GLM4模型调用
     *
     * @param sysMessage
     * @param userMessage
     * @param isStream
     * @param invokeMethod
     * @param temperature
     * @return
     */
    public String doSysChatRequest(String sysMessage
            , String userMessage
            , Boolean isStream
            , String invokeMethod
            , Float temperature
    ) {
        return doSysRequest(sysMessage, userMessage, Constants.ModelChatGLM4, isStream, invokeMethod, temperature);
    }


    /**
     * 包含系统，用户信息请求，返回值被解封为仅返回信息
     *
     * @param sysMessage
     * @param userMessage
     * @param model
     * @param isStream
     * @param invokeMethod
     * @param temperature
     * @return
     */
    public String doSysRequest(String sysMessage
            , String userMessage
            , String model
            , Boolean isStream
            , String invokeMethod
            , Float temperature
    ) {
        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), sysMessage));
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), userMessage));


            ModelApiResponse modelApiResponse = doBaseRequest(messages, model, isStream, invokeMethod, temperature);
            String result = modelApiResponse.getData().getChoices().get(0).getMessage().getContent().toString();
            return result;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 最基础的AI调用
     *
     * @param messages     给AI的消息列表
     * @param model        调用的模型
     * @param isStream     是否开启SSE调用
     * @param invokeMethod 调用方式
     * @param temperature  回答的发散程度
     * @return 返回信息封装类
     */
    public ModelApiResponse doBaseRequest(
            List<ChatMessage> messages
            , String model
            , Boolean isStream
            , String invokeMethod
            , Float temperature
    ) {
        try {


            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .stream(isStream)
                    .invokeMethod(invokeMethod)
                    .temperature(temperature)
                    .messages(messages)
                    .build();

            return client.invokeModelApi(chatCompletionRequest);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }


}



