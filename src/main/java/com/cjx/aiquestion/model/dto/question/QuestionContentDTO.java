package com.cjx.aiquestion.model.dto.question;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * @author cjx
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionContentDTO {
    private String title;
    private List<Option> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        private String result;
        private int score;
        private String value;
        private String key;
    }
}