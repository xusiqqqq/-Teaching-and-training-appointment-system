package com.kclm.xsap.entity;

import java.util.Date;
import lombok.Data;

@Data
public class TReservationRecord extends BaseEntity {

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

    /**
    * 会员id
    */
    private Long memberId;

    /**
    * 排课记录id
    */
    private Long scheduleId;

}