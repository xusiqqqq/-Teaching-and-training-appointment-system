package com.kclm.xsap.entity;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TRechargeRecord extends BaseEntity {

	/**
    * 关联的会员
    */
    private List<TMember> members;
	
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

}