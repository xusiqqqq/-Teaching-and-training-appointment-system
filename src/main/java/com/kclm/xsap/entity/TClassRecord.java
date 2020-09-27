package com.kclm.xsap.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_class_record",resultMap = "TClassRecordMap")
public class TClassRecord extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 *  封装会员实体数据
	 */
	@TableField(exist = false)
	@ToString.Exclude
    private TMember member;
	
	/**
	 * 会员卡名
	 */
	private String cardName;
	
	/**
	 * 排课记录id
	 */
	private Long scheduleId;

	 /**
	 *  封装排课计划实体数据 
	 */
    @TableField(exist = false)
	@ToString.Exclude
	private TScheduleRecord schedule;
	
	private String note;
	
	/**
	 * 教师评语
	 */
	private String comment;
	
	/**
	 * 上课状态检定。1，已上课；0，未上课
	 */
	private Integer checkStatus;
	
}
