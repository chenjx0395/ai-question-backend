package com.cjx.aiquestion.model.dto.scoringResult;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建评分结果请求
 *
 * @author cjx
 *
 */
@Data
public class ScoringResultAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}