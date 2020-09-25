package com.kclm.xsap.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月17日 上午11:34:17 
 * @description 此类用来描述了首页关于统计的数据
 *
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class HomePageDTO {

	/**
	 *  起始日期
	 */
	private LocalDate startDate;
	
	/**
	 * 结束日期
	 */
	private LocalDate endDate;
	
	/**
	 * 会员总数
	 */
	private Integer totalMembers;
	
	/**
	 * 活跃用户（近一个月有过预约的用户）
	 */
	private Integer activeMembers;
	
	/**
	 * 预约总数
	 */
	private Integer totalReservations;
	
	/**
	 * 每日约课数量，用区间展示
	 */
	private List<Integer> dailyReservations;
	
	/**
	 * 每日新增会员数量，用区间展示
	 */
	private List<Integer> dailyNewMembers;
	
}
