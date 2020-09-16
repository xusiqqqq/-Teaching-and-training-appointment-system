package com.kclm.xsap.service;

import java.util.List;

import com.kclm.xsap.entity.TCourse;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月15日 下午3:27:52 
 * @description 此类用来描述了团课管理业务
 *
 */
public interface CourseService {

	boolean save(TCourse card);
	
	boolean deleteById(Integer id);
	
	boolean update(TCourse course);
	
	/**
	 * 分页查询。获取所有的课程信息
	 * @param currentPage
	 * @param pageSize
	 * @return List<TCourse>。课程信息结果集
	 */
	List<TCourse> findAllByPage(Integer currentPage,Integer pageSize);
	
	/**
	 * 获取所有的课程信息
	 * @return List<TCourse>。课程信息结果集
	 */
	List<TCourse> findAll();
}
