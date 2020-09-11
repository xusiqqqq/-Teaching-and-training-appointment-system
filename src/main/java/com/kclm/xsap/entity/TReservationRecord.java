package com.kclm.xsap.entity;


import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_reservation_record",resultMap = "TReservationRecordMap")
public class TReservationRecord extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
    * 关联的排课记录
    */
    private Integer scheduleId;
	
    /**
	 *  封装排课计划实体数据 
	 */
    @TableField(exist = false)
	private TScheduleRecord schedule;
	
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
    * 预约人数
    */
    private Integer orderNums;

    /**
    * 预约状态，1有效，0无效
    */
    private Boolean status;

    /**
    * 教师评语
    */
    private String comment;

    private String note;

    /**
    * 操作员
    */
    private String operator;

}