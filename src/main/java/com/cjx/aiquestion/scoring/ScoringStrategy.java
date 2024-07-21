package com.cjx.aiquestion.scoring;

import com.cjx.aiquestion.model.entity.App;
import com.cjx.aiquestion.model.entity.UserAnswer;

import java.util.List;

/**
 * @author cjx
 */
public interface ScoringStrategy {

    /**
     * 执行评分
     *
     * @param choices
     * @param app
     * @return
     * @throws Exception
     */
    UserAnswer doScore(List<String> choices, App app) throws Exception;
}