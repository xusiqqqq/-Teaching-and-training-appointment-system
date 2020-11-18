package com.kclm.xsap.entity;


import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_reservation_record")
public class TReservationRecord extends BaseEntity implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
    * 关联的排课记录
    */
    private Long scheduleId;

    /**
	 *  封装排课计划实体数据
	 */
    @TableField(exist = false)
	@ToString.Exclude
	private TScheduleRecord schedule;

    /**
     * 关联的会员
     */
	private Long memberId;

	/**
	 *  封装会员实体数据
	 */
	@TableField(exist = false)
	@ToString.Exclude
    private TMember member;

	/**
	 * 	由会员指定的会员卡进行预约
	 */
	private String cardName;

    /**
    * 预约状态，1有效，0无效
    */
    private Integer status;

    /**
     * 单次操作预约人数，默认 0
     */
    private Integer reserveNums;

    /**
     *  取消次数
     */
    private Integer cancelTimes;

    /**
    * 教师评语
    */
    private String comment;

    private String note;

    /**
     * 上课备注，放在这里方便上课记录的录入
     */
    private String classNote;

    /**
    * 操作员
    */
    private String operator;

}
