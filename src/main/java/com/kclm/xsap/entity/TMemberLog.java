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
@TableName(value = "t_member_log")
public class TMemberLog extends BaseEntity implements Serializable{

	/**
	 *
	 */
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
    private String type;

    /**
     * 影响的金额
     */
    private BigDecimal involveMoney;

    /**
    * 操作员名称
    */
    private String operator;

}
