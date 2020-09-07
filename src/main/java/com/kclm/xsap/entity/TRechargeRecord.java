package com.kclm.xsap.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class TRechargeRecord extends BaseEntity {

    /**
    * 充值可用次数
    */
    private Integer addCount;

    /**
    * 延长有效天数
    */
    private Integer addDay;

    /**
    * 实收金额
    */
    private BigDecimal receivedMoney;

    private String note;

    /**
    * 会员id
    */
    private Long memberId;

}