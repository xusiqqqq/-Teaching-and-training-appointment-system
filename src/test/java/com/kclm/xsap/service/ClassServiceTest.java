package com.kclm.xsap.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kclm.xsap.entity.TClassRecord;

@SpringBootTest
public class ClassServiceTest {

	@Autowired
	private ClassService classService;
	
	//添加上课记录，根据预约记录进行添加
	@Test
	public void save() {
		classService.save();
	}
	
	//更新上课记录
	@Test
	public void update() {
		TClassRecord classed = new TClassRecord();
		classed.setId(2L);
		classed.setComment("--及格");
		classService.update(classed);
	}
	
	//更新所有“未确认”上课记录
	@Test
	public void updateAll() {
		classService.updateAll();
	}
	
}
