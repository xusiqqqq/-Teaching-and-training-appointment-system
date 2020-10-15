package com.kclm.xsap.service;

import java.util.List;

import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.entity.TEmployee;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月15日 上午10:38:54 
 * @description 此类用来描述了老师管理业务
 *
 */
public interface TeacherService {

	boolean save(TEmployee emp);

	boolean deleteById(Long id);
	
	boolean update(TEmployee emp);
	
	/**
	 * 	头像切换，更新操作
	 * @param  emp 员工信息
	 * @return
	 */
	boolean avatarChange(TEmployee emp);
	
	/**
	 *  获取所有教师信息
	 * @return List<TEmployee>。教师信息结果集
	 */
	List<TEmployee> findAll();
	
	/**
	 *  分页查询。获取所有教师信息
	 * @param currentPage 当前页码
	 * @param pageSize 每页展示数据个数
	 * @return List<TEmployee>。教师信息结果集
	 */
	List<TEmployee> findAllByPage(Integer currentPage,Integer pageSize);
	
	/**
	 *  当前教师的分析信息
	 * @param id 教职员工id
	 * @return TEmployee。教师信息
	 */
	TEmployee getAnalysis(Long id);
	
	/**
	 *  当前教师的上课记录
	 * @param id 教职员工id
	 * @return List<ClassRecordDTO>。上课记录结果集
	 */
	List<ClassRecordDTO> listClassRecord(Long id);
}
