package com.cjx.aiquestion.model.dto.scoringresult;

import lombok.Data;

/**
 * @author cjx
 */
@Data
public class QuestionAnswerDTO {

    /**
     * 题目
     */
    private String title;

    /**
     * 用户答案
     */
    private String userAnswer;
}