package com.kclm.xsap.entity;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TReservationRecord extends BaseEntity {

	/**
    * 关联的排课记录
    */
    private List<TScheduleRecord> schedules;
	
    /**
     * 关联的会员
     */
    private List<TMember> members;
    
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