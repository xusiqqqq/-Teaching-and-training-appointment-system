package com.kclm.xsap.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kclm.xsap.entity.TCourse;

import java.util.List;

@SpringBootTest
public class TCourseMapperTest {

	@Autowired
	private TCourseMapper courseMapper;
	
	@Test
	public void testSave() {
		TCourse course = new TCourse();
		course.setName("数学");
		course.setDuration(45);
		course.setColor("blue");
		course.setContains(6);
		course.setLimitSex("男");
		course.setLimitAge(6);
		course.setIntroduce("语文课");
		course.setTimesCost(1);
		
		System.out.println(courseMapper);
		courseMapper.insert(course);
	}

	@Test
	public void testSelectAll() {
		final List<TCourse> tCourses = courseMapper.selectList(null);
		//
		if(tCourses != null) {
			tCourses.forEach(System.out::println);
		}
	}
}
