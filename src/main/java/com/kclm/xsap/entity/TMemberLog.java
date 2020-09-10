package com.kclm.xsap.entity;


import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TMemberLog extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
    * 关联的会员
    */
	private Integer memberId;
	/**
	 *  封装会员实体数据
	 */
	@TableField(exist = false)
    private TMember member;
	
    /**
    * 操作类型
    */
    private String type;

    /**
    * 操作员名称
    */
    private String operator;

}