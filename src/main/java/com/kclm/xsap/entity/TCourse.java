package com.kclm.xsap.entity;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TCourse extends BaseEntity {

    /**
     * 关联的会员卡
     */
    private List<TMemberCard> cards; 
	
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

}