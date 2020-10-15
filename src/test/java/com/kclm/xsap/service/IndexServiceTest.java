package com.kclm.xsap.service;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kclm.xsap.dto.HomePageDTO;
import com.kclm.xsap.dto.ReportDTO;

@SpringBootTest
public class IndexServiceTest {

	@Autowired
	IndexService indexService;
	
	//--------涉及到convert
	@Test
	public void queryByDate() {
		LocalDate start = LocalDate.of(2020, 9, 16);
		LocalDate end = LocalDate.of(2020, 10, 8);
		HomePageDTO homePage = indexService.queryByDate(start, end);
		System.out.println("---------");
		System.out.println(homePage);
		System.out.println("---------");
	}
	
	//--------涉及到convert
	@Test
	public void statistic() {
		List<ReportDTO> statistic = indexService.statistic();
		System.out.println("报表数据：" + statistic);
		for (ReportDTO report : statistic) {
			System.out.println("report :" + report);
		}
	}
	
}
