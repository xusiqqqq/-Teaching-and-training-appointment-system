package com.kclm.xsap.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class TRechargeRecord extends BaseEntity {
    private Long id;

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

    private Date createTime;

    private Date lastModifyTime;

    private Integer version;
}