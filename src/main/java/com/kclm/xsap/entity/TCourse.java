package com.kclm.xsap.entity;

import java.util.Date;
import lombok.Data;

@Data
public class TCourse extends BaseEntity {
    private Long id;

    private String name;

    /**
    * 课程时长
    */
    private Integer duration;

    /**
    * 上课人数
    */
    private Integer contains;

    /**
    * 卡片颜色
    */
    private String color;

    /**
    * 课程介绍
    */
    private String introduce;

    /**
    * 每节课程需花费的次数
    */
    private Integer timesCost;

    /**
    * 限制性别
    */
    private String limitSex;

    /**
    * 限制年龄
    */
    private Integer limitAge;

    /**
    * 限制预约次数
    */
    private Integer limitCounts;

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