package com.kclm.xsap.service;
/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月15日 上午10:12:22 
 * @description 此类用来描述了首页业务功能
 * 
 */

import java.time.LocalDate;
import java.util.List;

import com.kclm.xsap.dto.HomePageDTO;
import com.kclm.xsap.dto.ReportDTO;
import com.kclm.xsap.entity.BaseEntity;

public interface IndexService {
	
	/**
	 * 查询首页的报表数据
	 * @param start 起始日期
	 * @param end 结束日期
	 * @return List<HomePageDTO>。
	 */
	List<HomePageDTO> queryByDate(LocalDate start,LocalDate end);
	
	/**
	 *  会员卡统计
	 * @return ReportDTO。
	 */
	ReportDTO statistic();
}
