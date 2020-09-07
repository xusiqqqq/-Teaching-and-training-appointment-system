package com.kclm.xsap.entity;

import java.util.Date;
import lombok.Data;

@Data
public class TConsumeRecord extends BaseEntity {

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
    * 操作员
    */
    private String operator;

    private String note;

    /**
    * 会员id
    */
    private Long memberId;

}