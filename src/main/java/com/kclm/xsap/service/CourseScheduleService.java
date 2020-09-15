package com.kclm.xsap.service;

import java.util.List;

import com.kclm.xsap.entity.TScheduleRecord;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月15日 下午3:44:42 
 * @description 此类用来描述了团课排课管理业务
 *
 */
public interface CourseScheduleService {

	boolean save(TScheduleRecord schedule);

	boolean remove(Integer id);
	
	/**
	 * 获取所有的排课记录。查询分类有：日、周、月
	 * @param locate：指定的日期号
	 * @param type：查询的类型。0：天（默认）；1：周；2：月
	 * @return List<TScheduleRecord>。团课排课记录结果集
	 */
	List<TScheduleRecord> getScheduleList(Integer locate,Integer type);
	
}
