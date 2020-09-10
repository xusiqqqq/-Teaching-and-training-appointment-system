package com.kclm.xsap.mapper;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kclm.xsap.entity.TMemberBindRecord;


@SpringBootTest
public class TestOf {

	@Autowired
	private TMemberBindRecordMapper tbr;
	
	@Test
	public void justTest() {
		List<TMemberBindRecord> list = tbr.findAll();
		for (TMemberBindRecord bind : list) {
			System.out.println("-------"+bind);
		}
		
	}
	
}
