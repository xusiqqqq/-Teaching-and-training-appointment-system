package com.kclm.xsap.web.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.CourseScheduleDTO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.dto.ScheduleVO;
import com.kclm.xsap.entity.TClassRecord;
import com.kclm.xsap.entity.TConsumeRecord;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TMemberBindRecordMapper;
import com.kclm.xsap.service.ClassService;
import com.kclm.xsap.service.CourseScheduleService;
import com.kclm.xsap.service.CourseService;
import com.kclm.xsap.service.MemberCardService;
import com.kclm.xsap.service.ReserveService;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

	//文件夹名
	private static final String folderName = "course/";
	
	@Autowired
	CourseScheduleService scheduleServie;
	
	@Autowired
	ReserveService reserveService;
	
	@Autowired
	MemberCardService cardService;
	
	@Autowired
	ClassService classService;
	
	@Autowired
	CourseService courseService;
	
	/*========页面跳转=======*/
	//=》课程表概览页
	@RequestMapping("/x_course_schedule.do")
	public String x_course_schedule() {
		return folderName + "x_course_schedule";
	}
	
	//=》课程表详情页
	@RequestMapping("/x_course_schedule_detail.do")
	public String x_course_schedule_detail(Long id,Model model) {
		model.addAttribute("ID", id);
		return folderName + "x_course_schedule_detail";
	}
	
	/*========业务处理=======*/
	//课程表预加载
	@ResponseBody
	@RequestMapping("/scheduleList.do")
	public List<ScheduleVO> scheduleList(){
		List<ScheduleVO> scheduleVoList = new ArrayList<>();
		List<CourseScheduleDTO> scheduleAll = scheduleServie.listScheduleAll();
		for (CourseScheduleDTO scheduleDTO : scheduleAll) {
			ScheduleVO scheduleVo = new ScheduleVO();
			//1、拿到课程名
			String title = scheduleDTO.getCourseName();
			//2、拿到开始时间
			String start = scheduleDTO.getStartTime().toString();
			//3、拿到结束时间
			String end = scheduleDTO.getEndTime().toString();
			//4、拿到课程的显示颜色
			TCourse course = courseService.findById(scheduleDTO.getCourseId());
			String color = "#60e374";	//默认颜色
			if(course != null) {
				color = course.getColor();				
			}
			//5、显示的字体颜色
			String textColor = "#FFFFFF";	//默认颜色
			//6、拿到url
			String url = "/xsap/schedule/x_course_schedule_detail.do?id=";
			url +=  scheduleDTO.getScheduleId();
			//设置vo
			scheduleVo.setTitle(title);
			scheduleVo.setStart(start);
			scheduleVo.setEnd(end);
			scheduleVo.setColor(color);
			scheduleVo.setTextColor(textColor);
			scheduleVo.setUrl(url);
			scheduleVoList.add(scheduleVo);
		}
		
		return scheduleVoList;
	}
	
	//新增排课
	@ResponseBody
	@RequestMapping("/scheduleAdd.do")
	public TScheduleRecord scheduleAdd(TScheduleRecord schedule) {
		//检测用。version 4：新增排课成功
		TScheduleRecord checkFor = new TScheduleRecord();
		if(schedule.getCourseId() == null) {
			checkFor.setLimitSex("未选择课程");
			return checkFor;
		}
		if(schedule.getTeacherId() == null) {
			checkFor.setLimitSex("未选择老师");
			return checkFor;
		}
		if(schedule.getStartDate() == null) {
			checkFor.setLimitSex("未选择上课日期");
			return checkFor;
		}
		if(schedule.getClassTime() == null) {
			checkFor.setLimitSex("未选择上课时间");
			return checkFor;
		}
		
		boolean result = scheduleServie.save(schedule);
		if(!result) {
			checkFor.setLimitSex("此堂课一小时内已经排过");
			return checkFor;
		}
		checkFor.setVersion(4);
		return checkFor;
	}
	
	//复制排课
	@ResponseBody
	@RequestMapping("/scheduleCopy.do")
	public TScheduleRecord scheduleCopy(String sourceDateStr, String targetDateStr) {
		//检测用。version 4：新增排课成功
		TScheduleRecord checkFor = new TScheduleRecord();
		
		LocalDate sourceDate = LocalDate.parse(sourceDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		LocalDate targetDate = LocalDate.parse(targetDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		boolean result = scheduleServie.copySchedule(sourceDate, targetDate);
		if(!result) {
			checkFor.setLimitSex("当前日期没有课，或范围错误，或目标日期已排课");
			return checkFor;
		}
		checkFor.setVersion(4);
		return checkFor;
	}
	
	/* ------------课程表详情------------- */
	//课程表详情 - 基本信息
	@ResponseBody
	@RequestMapping("/scheduleDetail.do")
	public CourseScheduleDTO scheduleDetail(Long id,Model model) {	
		CourseScheduleDTO scheduleDTO = scheduleServie.findById(id);
		return scheduleDTO;
	}
	//课程表详情 - 已预约
	@ResponseBody
	@RequestMapping("/reservedList.do")
	public List<ReserveRecordDTO> reservedList(Long id){
		CourseScheduleDTO scheduleDTO = scheduleServie.findById(id);
		List<ReserveRecordDTO> reservedList = null;
		if(scheduleDTO != null) {
			reservedList = scheduleDTO.getReservedList();
		}
		return reservedList;
	}
	
	//课程表详情 - 预约记录
	@ResponseBody
	@RequestMapping("/reserveRecord.do")
	public List<ReserveRecordDTO> reserveRecord(Long id){
		CourseScheduleDTO scheduleDTO = scheduleServie.findById(id);
		List<ReserveRecordDTO> reserveRecord = null;
		if(scheduleDTO != null) {
			reserveRecord = scheduleDTO.getReserveRecord();	
		}
		return reserveRecord;
	}
	
	//课程表详情 - 上课数据
	@ResponseBody
	@RequestMapping("/classRecord.do")
	public List<ClassRecordDTO> classRecord(Long id){
		CourseScheduleDTO scheduleDTO = scheduleServie.findById(id);
		List<ClassRecordDTO> classRecord = null;
		if(scheduleDTO != null) {
			classRecord = scheduleDTO.getClassRecord();		
		}
		return classRecord;
	}
	
	/* ------------off------------- */
	
	//上课记录确认扣费
	@ResponseBody
	@RequestMapping("/consumeEnsure.do")
	public TConsumeRecord consumeOpt(TConsumeRecord consume,Long classId,Long cardId	) {
		//提示用。version 4：成功
		TConsumeRecord checkOnly = new TConsumeRecord();
		
		System.out.println("---------");
		System.out.println(consume);
		consume.setCardId(cardId);
		consume.setOperator("某某某操作");
		consume.setOperateType("上课扣费");
		
		//消费行为产生
		cardService.consume(consume);
		//上课记录变为”已确认“
		classService.update(classId, 1);
		
		//确认上课后，对其已预约的记录进行“不可操作”处理
		//判断当前”已预约“记录是否已经“确认上课“
		TClassRecord isEnsure = classService.findIsEnsureById(classId);
		if(isEnsure != null) {
			checkOnly.setNote("ensure");
		}
		
		checkOnly.setVersion(4);			
		return checkOnly;
	}
	
	//一键确认 -- 当期排课，所有”未确认“的上课记录确认扣费
	@ResponseBody
	@RequestMapping("/consumeEnsureAll.do")
	public TConsumeRecord classCheckList(Long scheduleId) {
		//提示用。version 4：成功
		TConsumeRecord checkOnly = new TConsumeRecord();
		if(scheduleId != null){
			classService.ensureByScheduleId(scheduleId);			
			checkOnly.setVersion(4);			
		}
		
		return checkOnly;
	}
	
	
	//删除当前课程计划
	@ResponseBody
	@RequestMapping("/deleteOne.do")
	public String deleteOne(Long id) {
		System.out.println("delete id : " + id);
		/*--------------业务说明-------------*/
		/*
		 	仅当“确认上课”后，才进行卡次的扣除，所以删除课程，不再对卡次进行退还
		 */
			//退还所有已预约此课的会员的消费卡次数
//			CourseScheduleDTO scheduleDTO = scheduleServie.findById(id);
//			Integer timesCost = courseService.findById((scheduleDTO.getCourseId())).getTimesCost();
//			List<ReserveRecordDTO> reservedList = reserveService.listReserved(id);
//			System.out.println("--------------------------");
//			System.out.println(reservedList);
//			System.out.println("--------------------------");
//			for (ReserveRecordDTO reserved : reservedList) {
//				System.out.println("---------reserved----------");
//				System.out.println(reserved);
//				System.out.println("-------------------");
//				
//				Long cardId = cardService.findByName((reserved.getCardName())).getId();
//				Long memberId = reserved.getMemberId();
//				TMemberBindRecord bindRecord = cardService.findBindRecord(memberId, cardId);
//				bindRecord.setValidCount(bindRecord.getValidCount() + timesCost);
//				cardService.updateBindRecord(bindRecord);
//			}
			//退还完成后，再进行排课计划的删除
			scheduleServie.deleteById(id);
		return "yes";
	}
	
}
