package com.kclm.xsap.service;

import com.kclm.xsap.entity.TClassRecord;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月26日 下午4:10:16 
 * @description 此类用来描述了上课管理业务，用来保存上课记录
 *
 */
public interface ClassService {

	/**
	 * 	根据预约id，进行上课记录的单个录入
	 * @param reserveId	预约id
	 * @return
	 */
	boolean saveByReserveId(Long reserveId);
	
	/**
	 * 	针对“已预约”，进行全部录入
	 * @return
	 */
	boolean saveAll();
	
	/**
	 * 
	 * @param classId	上课记录id
	 * @param status	上课记录确认状态
	 * @return
	 */
	boolean update(Long classId,Integer status);
	
	boolean updateAll();
	
	//删除单个上课记录
	boolean deleteOne(Long classId);
	
}
