package com.kclm.xsap.web.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kclm.xsap.dto.HomePageDTO;
import com.kclm.xsap.dto.ReportDTO;
import com.kclm.xsap.service.IndexService;

import cn.hutool.Hutool;

@Controller
@RequestMapping("/index")
public class IndexController {

	@Autowired
	private IndexService indexService;  
	
	/* 页面跳转 */
	@RequestMapping("/x_index_home.do")
	public String toHomePage() {
		return "x_index_home";
	}
	
	/* 页面数据处理 */
	//首页基本数据
	@RequestMapping("/homePage.do")
	@ResponseBody
	public HomePageDTO homePape(String startDateStr,String endDateStr) {
		//test
//		LocalDate startDate1 = LocalDate.of(2020, 9, 16);
//		LocalDate endDate1 = LocalDate.of(2020, 10, 8);
//		HomePageDTO homeDto = indexService.queryByDate(startDate1, endDate1);
		//LocalDate转换
		System.out.println("startDateStr " + startDateStr);
		System.out.println("endDateStr " + endDateStr);
		
		LocalDate startDate = null;
		LocalDate endDate = null;
		
		if(startDateStr != null || endDateStr != null) {
			String format = "yyyy-MM-dd";
			startDate = LocalDate.parse(startDateStr,DateTimeFormatter.ofPattern(format));
			endDate = LocalDate.parse(endDateStr,DateTimeFormatter.ofPattern(format));
		}
		
		HomePageDTO homeDto = indexService.queryByDate(startDate, endDate);
		System.out.println("首页数据："+homeDto);
		return homeDto;
	}
	
	//报表数据
	@RequestMapping("/report.do")
	@ResponseBody
	public List<ReportDTO> report() {
		List<ReportDTO> reportDtoList = indexService.statistic();
		System.out.println("报表数据：" + reportDtoList);
		return reportDtoList;
	}
	
}
