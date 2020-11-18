package com.kclm.xsap.entity;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_course")
public class TCourse extends BaseEntity implements Serializable{

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 关联的会员卡, 不是数据库的列
     */
    @TableField(exist = false)
    @ToString.Exclude

    private List<TMemberCard> cardList;

    private String name;

    /**
    * 课程时长，单位：分钟
    */
    private Integer duration;

    /**
    * 课堂容纳人数
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
    * 限制年龄，-1 表示不限制
    */
    private Integer limitAge;

    /**
    * 限制预约次数，-1 表示不限制
    */
    private Integer limitCounts;

}
