package com.kclm.xsap.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author fangkai
 * @description
 * @create 2021-12-22 10:06
 */
@Data
@Accessors(chain = true)
public class ConsumeFormVo {
    private Long classId;

    private Long memberId;

    private Long cardBindId;

    private String Operator;

    /**
     * 扣除次数
     */
    @NotNull(message = "请输入扣除次数")
    private Integer cardCountChange;

    //自定义扣费时的消费金额
    private BigDecimal amountOfConsumption;

    private Integer cardDayChange;

    private String note;

}
