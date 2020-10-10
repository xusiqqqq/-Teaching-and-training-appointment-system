package com.kclm.xsap.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.dto.CourseDTO;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.mapper.TMemberCardMapper;

@SpringBootTest
public class CourseServiceTest {

	@Autowired
	CourseService courseService;
	
	@Autowired
	TMemberCardMapper cardMapper;
	
	@Test
	public void save() {
		TCourse course = new TCourse();
		course.setName("美术");
		course.setDuration(40);
		course.setContains(12);
		course.setColor("smoke");
		List<TMemberCard> cardList = cardMapper.selectList(new QueryWrapper<TMemberCard>().in("id", 1,2));
		course.setCardList(cardList);
		course.setIntroduce("美术课");
		course.setLimitSex("男");
		course.setLimitAge(6);
		course.setLimitCounts(3);
		courseService.save(course);
	}

	@Test
	public void deleteById() {
		courseService.deleteById(16L);
	}
	
	@Test
	public void update() {
		TCourse course = new TCourse();
		course.setId(16L);
		course.setName("体育");
		courseService.update(course);
	}

	//-------涉及到convert
	@Test
	public void findAllByPage() {
		List<CourseDTO> list = courseService.findAllByPage(1, 3);
		for (CourseDTO course : list) {
			System.out.println("---" + course);
		}
	}

	//-------涉及到convert
	@Test
	public void findAll() {
		List<CourseDTO> list = courseService.findAll();
		for (CourseDTO course : list) {
			System.out.println("---" + course);
		}
	}
	
}
