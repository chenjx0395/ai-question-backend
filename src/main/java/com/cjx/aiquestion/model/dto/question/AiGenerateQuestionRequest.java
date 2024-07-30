package com.cjx.aiquestion.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cjx
 */
@Data
public class AiGenerateQuestionRequest implements Serializable {

    /**
     * id
     */
    private Long appId;

    /**
     * 题目数
     */
    int questionNumber = 10;

    /**
     * 选项数
     */
    int optionNumber = 2;

    private static final long serialVersionUID = 1L;
}