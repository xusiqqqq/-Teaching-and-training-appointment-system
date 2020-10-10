
package com.kclm.xsap.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kclm.xsap.dto.CourseDTO;
import com.kclm.xsap.dto.convert.CourseConvert;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.service.CourseService;

@Service
@Transactional
public class CourseServiceImpl implements CourseService{

	@Autowired
	private TCourseMapper courseMapper;
	
	@Override
	public boolean save(TCourse course) {
		courseMapper.insert(course);
		//向中间表插入数据
		List<TMemberCard> cardList = course.getCardList();
		for (TMemberCard card : cardList) {
			courseMapper.insertMix(card.getId(), course.getId());
		}
		return true;
	}

	@Override
	public boolean deleteById(Long id) {
		//删除中间表关联的键
		courseMapper.deleteBindCard(id);
		//本表
		courseMapper.deleteById(id);
		return true;
	}

	@Override
	public boolean update(TCourse course) {
		courseMapper.updateById(course);
		//更新中间表: 先删除已绑定的，再重新绑定
		courseMapper.deleteBindCard(course.getId());
		//向中间表插入数据
		List<TMemberCard> cardList = course.getCardList();
		if(cardList != null) {			
			for (TMemberCard card : cardList) {
				courseMapper.insertMix(card.getId(), course.getId());
			}
		}
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
