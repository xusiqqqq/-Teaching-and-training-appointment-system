package com.kclm.xsap.service;

import java.util.List;

import com.kclm.xsap.entity.TGlobalReservationSet;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月15日 下午3:54:43 
 * @description 此类用来描述了团课预约设置业务
 *
 */
public interface GlobalReservationSetService {

	boolean update(TGlobalReservationSet glogal);
	
	/**
	 * 	根据id查询全局设置
	 * @return
	 */
	TGlobalReservationSet findOne(Long id);
	
	/**
	 * 获取所有的全局预约设置信息
	 * @return List<TGlobalReservationSet>。全局预约设置信息结果集
	 */
	List<TGlobalReservationSet> findAll();
}
