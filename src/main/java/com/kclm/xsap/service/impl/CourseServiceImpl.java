package com.kclm.xsap.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kclm.xsap.dto.CourseDTO;
import com.kclm.xsap.dto.convert.CourseConvert;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.service.CourseService;

public class CourseServiceImpl implements CourseService{

	@Autowired
	private TCourseMapper courseMapper;
	
	@Override
	public boolean save(TCourse course) {
		courseMapper.insert(course);
		return true;
	}

	@Override
	public boolean deleteById(Long id) {
		courseMapper.deleteById(id);
		return true;
	}

	@Override
	public boolean update(TCourse course) {
		courseMapper.updateById(course);
		return true;
	}

	@Override
	public List<CourseDTO> findAllByPage(Integer currentPage, Integer pageSize) {
		IPage<TCourse> page = new Page<>(currentPage,pageSize);
		IPage<TCourse> pageList = courseMapper.selectPage(page , null);
		List<TCourse> courseList = pageList.getRecords();
		
		List<CourseDTO> courseDtoList = new ArrayList<CourseDTO>();
		for (TCourse course : courseList) {
			//DTO转换
			CourseDTO courseDTO = CourseConvert.INSTANCE.entity2Dto(course);
			courseDtoList.add(courseDTO);
		}
		return courseDtoList;
	}

	@Override
	public List<CourseDTO> findAll() {
		List<TCourse> courseList = courseMapper.selectList(null);
		List<CourseDTO> courseDtoList = new ArrayList<CourseDTO>();
		for (TCourse course : courseList) {
			//DTO转换
			CourseDTO courseDTO = CourseConvert.INSTANCE.entity2Dto(course);
			courseDtoList.add(courseDTO);
		}
		return courseDtoList;
	}

}
