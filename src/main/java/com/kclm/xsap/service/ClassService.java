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
	 * 	根据预约的情况，进行上课记录的录入
	 */
	boolean save();
	
	boolean update(TClassRecord classed);
	
}
