package com.cjx.aiquestion.model.dto.useranswer;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建用户答题记录请求
 *
 * @author cjx
 *
 */
@Data
public class UserAnswerAddRequest implements Serializable {


    /**
     * 应用 id
     */
    private Long appId;



    /**
     * 用户答案（JSON 数组）
     */
    private List<String> choices;






    private static final long serialVersionUID = 1L;
}