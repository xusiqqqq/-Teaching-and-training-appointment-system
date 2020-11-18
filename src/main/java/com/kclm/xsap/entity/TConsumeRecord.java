package com.kclm.xsap.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_consume_record")
public class TConsumeRecord extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
    * 关联的会员绑定id
    */
	private Long memberBindId;

	/**
	 *  封装会员绑定实体数据
	 */
	@TableField(exist = false)
	@ToString.Exclude
    private TMemberBindRecord bindRecord;

    /**
    * 操作类型
    */
    private String operateType;

    /**
    * 卡次变化
    */
    private Integer cardCountChange;

    /**
    * 有效天数变化
    */
    private Integer cardDayChange;

    /**
     *  花费的金额
     */
    private BigDecimal moneyCost;

    /**
    * 操作员
    */
    private String operator;

    private String note;

}
