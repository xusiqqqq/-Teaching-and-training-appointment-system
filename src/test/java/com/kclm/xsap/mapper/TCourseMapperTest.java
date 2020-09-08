package com.kclm.xsap.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kclm.xsap.entity.TCourse;

@SpringBootTest
public class TCourseMapperTest {

	@Autowired
	private TCourseMapper courseMapper;
	
	@Test
	public void testSave() {
		TCourse course = new TCourse();
		course.setName("语文");
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
	
}
