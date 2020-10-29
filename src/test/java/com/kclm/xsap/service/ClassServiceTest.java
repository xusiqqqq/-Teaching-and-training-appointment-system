package com.kclm.xsap.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kclm.xsap.entity.TClassRecord;
import com.kclm.xsap.entity.TReservationRecord;

@SpringBootTest
public class ClassServiceTest {

	@Autowired
	private ClassService classService;
	
	@Test
	public void reserveClassSet() {
		TReservationRecord reserve = new TReservationRecord();
		reserve.setMemberId(1L);
		reserve.setScheduleId(1L);
		reserve.setStatus(1);
		classService.reserveClassSet(reserve);
	}
	
	//添加上课记录，根据预约记录进行添加
	@Test
	public void saveByReserveId() {
//		classService.saveByReserveId(1L);
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
