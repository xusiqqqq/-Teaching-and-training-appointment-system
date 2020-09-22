package com.kclm.xsap.service;

import java.util.List;

import com.kclm.xsap.dto.CourseDTO;
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

	boolean save(TCourse course);
	
	boolean deleteById(Long id);
	
	boolean update(TCourse course);
	
	/**
	 * 分页查询。获取所有的课程信息
	 * @param currentPage 当前页码
	 * @param pageSize 每页展示数据个数
	 * @return List<CourseDTO>。课程信息结果集
	 */
	List<CourseDTO> findAllByPage(Integer currentPage,Integer pageSize);
	
	/**
	 * 获取所有的课程信息
	 * @return List<TCourse>。课程信息结果集
	 */
	List<CourseDTO> findAll();
}
