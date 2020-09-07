package com.kclm.xsap.entity;

import java.util.Date;
import lombok.Data;

@Data
public class TConsumeRecord extends BaseEntity {
    private Long id;

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

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 修改时间
    */
    private Date lastModifyTime;

    /**
    * 版本
    */
    private Integer version;
}