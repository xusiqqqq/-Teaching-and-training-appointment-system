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
	public void saveByReserveId() {
		classService.saveByReserveId(1L);
	}
	
	//针对“已预约”，进行全部录入
	@Test
	public void saveAll() {
		classService.saveAll();
	}
	
	//更新上课记录
	@Test
	public void update() {
		classService.update(3L,1);
	}
	
	//更新所有“未确认”上课记录
	@Test
	public void updateAll() {
		classService.updateAll();
	}
	
	//删除单条预约记录
	@Test
	public void deleteOne() {
		classService.deleteOne(2L);
	}
	
}
