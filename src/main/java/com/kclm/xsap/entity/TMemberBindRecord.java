package com.kclm.xsap.entity;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TMemberBindRecord extends BaseEntity {

	/**
	 * 关联的会员
	 */
    private List<TMember> members;
    /**
     * 关联的会员卡
     */
    private List<TMemberCard> cards;

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