package com.kclm.xsap.web.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kclm.xsap.entity.TGlobalReservationSet;
import com.kclm.xsap.service.GlobalReservationSetService;

@Controller
@RequestMapping("/globalSet")
public class GlobalSetController {

	//文件夹名
	private static final String folderName = "course/";
	
	@Autowired
	GlobalReservationSetService globalService;
	
	/*========页面跳转=======*/
	//=》全局预约页
	@RequestMapping("/x_course_reservation.do")
	public String x_course_reservation(Model model) {
		TGlobalReservationSet findOne = globalService.findOne(1L);
		model.addAttribute("GLOBAL_SET", findOne);

		String endTimeStr = findOne.getEndTime().toString();
		model.addAttribute("endTimeStr", endTimeStr);
		String cancelTimeStr = findOne.getCancelTime().toString();
		model.addAttribute("cancelTimeStr", cancelTimeStr);
		
		return folderName + "x_course_reservation";
	}
	
	/*========业务处理=======*/
	@ResponseBody
	@RequestMapping("/globalSetUpdate.do")
	public TGlobalReservationSet globalSetUpdate(TGlobalReservationSet globalSet,String start,String end,String cancel) {
		TGlobalReservationSet checkSet = new TGlobalReservationSet();
		System.out.println("-----before----");
		System.out.println(globalSet);
		System.out.println("---------");
		
		//预约开始时间
		if(start.equals("-1")) {
			globalSet.setStartDay(-1);
		}
		//预约截止时间
		if(end.equals("-1")) {
			globalSet.setEndHour(-1);
			globalSet.setEndDay(-1);
		}else if(end.equals("endHour")) {
			globalSet.setEndDay(-1);
		}else if(end.equals("endDay")) {
			globalSet.setEndHour(-1);
		}
		//取消预约时间
		if(cancel.equals("-1")) {
			globalSet.setCancelDay(-1);
			globalSet.setCancelHour(-1);
		}else if(cancel.equals("cancelHour")) {
			globalSet.setCancelDay(-1);
		}else if(cancel.equals("cancelDay")) {
			globalSet.setCancelHour(-1);
		}
		
		System.out.println("-----after----");
		System.out.println(globalSet);
		System.out.println("---------");
		//数据库最近的值
		TGlobalReservationSet oldOne = globalService.findOne(globalSet.getId());
		globalSet.setCreateTime(oldOne.getCreateTime());
		globalSet.setLastModifyTime(LocalDateTime.now());
		globalSet.setVersion(oldOne.getVersion());
		
		globalService.update(globalSet);
		checkSet.setVersion(4);
		return checkSet;
	}
	
}
