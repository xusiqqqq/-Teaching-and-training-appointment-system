package com.kclm.xsap.dto;

import java.util.List;

import com.kclm.xsap.entity.TMemberCard;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月17日 下午3:55:48 
 * @description 此类用来描述了课程信息
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class CourseDTO {

	private Long id;
	/**
	 * 课程名
	 */
	private String name;
	
	/**
	 * 课程的时长
	 */
	private Integer duration;
	
	/**
	 * 课堂最多容纳人数
	 */
	private Integer contains;
	
	/**
	 * 展示的背景颜色
	 */
	private String color;
	
	/**
	 * 	绑定会员卡
	 */
	private List<TMemberCard> cardList;
	
	/**
	 * 介绍
	 */
	private String introduce;
	
	/**
	 * 需消耗的卡次/节
	 */
	private Integer timesCost;
	
	/**
	 * 限制的性别
	 */
	private String limitSex;
	
	/**
	 * 限制的年龄
	 */
	private Integer limitAge;
	
	/**
	 * 限制的预约次数
	 */
	private Integer limitCounts;
	
}
