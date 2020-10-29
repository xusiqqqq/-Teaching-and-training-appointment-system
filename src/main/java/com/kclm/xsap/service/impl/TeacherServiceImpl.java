package com.kclm.xsap.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.entity.TEmployee;
import com.kclm.xsap.mapper.TEmployeeMapper;
import com.kclm.xsap.service.TeacherService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class TeacherServiceImpl implements TeacherService{

	@Autowired
	TEmployeeMapper employeeMapper;

//	@Autowired
//	private ClassRecordConvert classRecordConvert;
		
	@Override
	public boolean save(TEmployee emp) {
		employeeMapper.insert(emp);
		return true;
	}

	@Override
	public boolean deleteById(Long id) {
		TEmployee employee = employeeMapper.selectById(id);
		if(employee == null) {
			System.out.println("------无此条教师记录");
			return false;
		}
		employeeMapper.deleteById(id);
		return true;
	}

	@Override
	public boolean update(TEmployee emp) {
		employeeMapper.update(emp,new QueryWrapper<TEmployee>().eq("id",emp.getId()));
		return true;
	}

	@Override
	public List<TEmployee> findAll() {
		List<TEmployee> list = employeeMapper.selectList(null);
		return list;
	}

	@Override
	public List<TEmployee> findAllByPage(Integer currentPage, Integer pageSize) {
		//分页查询
		IPage<TEmployee> page = new Page<>(currentPage, pageSize);
		IPage<TEmployee> pageList = employeeMapper.selectPage(page , null);
		//获取到根据分页条件查询出的结果
		List<TEmployee> list = pageList.getRecords();
		return list;
	}

	/* 具体返回数据待定 */
	@Override
	public TEmployee getAnalysis(Long id) {
		TEmployee emp = employeeMapper.selectById(id);
		return emp;
	}

	//上课记录
	@Override
	public List<ClassRecordDTO> listClassRecord(Long id) {
		List<ClassRecordDTO> classDtoList = employeeMapper.listClassView(id);
		for (ClassRecordDTO classDto : classDtoList) {
			classDto.setClassTime(LocalDateTime.of(classDto.getStartDate(), classDto.getStartTime()));
		}
		return classDtoList;
	}

	//头像切换
	@Override
	public boolean avatarChange(TEmployee emp) {
		TEmployee employee = employeeMapper.selectById(emp.getId());
		if(employee == null) {
			log.debug("--------没有此员工");
			return false;
		}
		employeeMapper.updateById(employee);
		return true;
	}

}
