package com.kclm.xsap.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class TMemberBindRecord extends BaseEntity {

    private Long memberId;

    private Long cardId;

    /**
    * 充值可使用次数
    */
    private Integer addCount;

    /**
    * 有效期，按天算
    */
    private Integer validDay;

    /**
    * 实收金额
    */
    private BigDecimal receivedMoney;

    private String note;

}