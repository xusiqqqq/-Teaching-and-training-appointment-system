package com.kclm.xsap.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kclm.xsap.entity.TGlobalReservationSet;

@SpringBootTest
public class GlobalReservationSetServiceTest {

	@Autowired
	GlobalReservationSetService globalService;
	
	//更新
	@Test
	public void update() {
		TGlobalReservationSet global = new TGlobalReservationSet();
		global.setStartDay(8);
		global.setEndHour(1);
		global.setEndDay(2);
		global.setCancelHour(14);
		global.setCancelDay(1);
		globalService.update(global);
	}
	
	//查询所有
	@Test
	public void findAll() {
		List<TGlobalReservationSet> list = globalService.findAll();
		for (TGlobalReservationSet global : list) {
			System.out.println("--global: "+ global);
		}
	}
	
}
