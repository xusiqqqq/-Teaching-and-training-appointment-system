package com.kclm.xsap.web.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kclm.xsap.dto.CourseScheduleDTO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TClassRecord;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.service.ClassService;
import com.kclm.xsap.service.CourseScheduleService;
import com.kclm.xsap.service.ReserveService;

@Controller
@RequestMapping("/reserve")
public class ReserveController {

	@Autowired
	ReserveService reserveService;
	
	@Autowired
	ClassService classService;
	
	@Autowired
	CourseScheduleService scheduleService;
	
	//添加会员预约
	@ResponseBody
	@RequestMapping("/addReserve.do")
	public TReservationRecord addReserve(TReservationRecord reserve) {
		//检测用。version 4，成功
		TReservationRecord checkFor  = new TReservationRecord();
		
		/*
		 	对主要功能进行测试，性别限制、年龄限制的预约判断暂且不写
		 */
		
		//会员卡支持判断
		CourseScheduleDTO scheduleDTO = scheduleService.findById((reserve.getScheduleId()));
		if(!scheduleDTO.getSupportCards().contains(reserve.getCardName())) {
			checkFor.setComment("当前课程不支持此会员卡");
			return checkFor;
		}
		
		//未选择任何会员
		if(reserve.getMemberId() == null){
			checkFor.setComment("未选择任何会员，请选定");
			return checkFor;
		}
		//未选择任何会员卡
		if(reserve.getCardName() == null){
			checkFor.setComment("未选择任何会员卡，请选定");
			return checkFor;
		}
		
		//当前预约创建时间
		reserve.setCreateTime(LocalDateTime.now());
		
		Integer result = reserveService.save(reserve);
		if(result == -2) {
			checkFor.setComment("当前课程已预约满");
			return checkFor;
		}
		if(result == -1) {
			checkFor.setComment("已预约过当前课程");
			return checkFor;
		}
		if(result == 1) {
			checkFor.setComment("预约过早");
			return checkFor;
		}
		if(result == 2) {
			checkFor.setComment("预约过晚");
			return checkFor;
		}
		
		checkFor.setVersion(4);
		return checkFor;
	}
	
	//预约取消操作
	@ResponseBody
	@RequestMapping("/cancelReserve.do")
	public TReservationRecord cancelReserve(Long reserveId) {
		//检测用。version 4，成功
		TReservationRecord checkFor  = new TReservationRecord();
		
		TReservationRecord findOne = reserveService.findOne(reserveId);
		
		Long memberId = findOne.getMemberId();
		Long scheduleId = findOne.getScheduleId();
		TClassRecord findIsEnsure = classService.findIsEnsure(memberId, scheduleId);
		if(findIsEnsure != null) {
			checkFor.setComment("当前预约记录，已被确认为：已上课，无法取消！");
			checkFor.setVersion(5);
			return checkFor;
		}
		
		findOne.setStatus(0);
		findOne.setLastModifyTime(LocalDateTime.now());
		
		Integer result = reserveService.update(findOne);
		if(result == -2) {
			checkFor.setComment("当前课程已预约满");
			return checkFor;
		}
		if(result == -1) {
			checkFor.setComment("当前课程已达预约上限或当前课的取消次数达上限");
			return checkFor;
		}
		if(result == 3) {
			checkFor.setComment("取消预约时间过晚");
			return checkFor;
		}
		
		checkFor.setVersion(4);
		return checkFor;
	}
	
	/* -----------导出数据------------*/
	
	//导出当前排课的预约计划
	@ResponseBody
	@RequestMapping("/exportReserve.do")
	public String exportReserve(Long scheduleId) {
		List<ReserveRecordDTO> exportRecord = reserveService.listExportRecord(scheduleId);
		if(exportRecord != null && exportRecord.size() > 0) {
			//导出数据
			
			return "yes";
		}
		return "no";
	}
	
	//批量导出预约计划
	@ResponseBody
	@RequestMapping("/exportReserveList.do")
	public  String exportReserveList(String startDateStr, String endDateStr) {
		LocalDate startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		LocalDate endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		List<ReserveRecordDTO> exportRecordRange = reserveService.listExportRecordRange(startDate, endDate);
		if(exportRecordRange != null && exportRecordRange.size() > 0) {
			//导出数据
			
			return "yes";
		}
		return "no";
	}
	
	@ResponseBody
	@RequestMapping("/getReserveId.do")
	public TReservationRecord getReserveId(Long memberId, Long scheduleId) {
			TReservationRecord findOne = reserveService.findOneByMS(memberId, scheduleId);
			return findOne;
	}
	
}
