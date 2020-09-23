package com.kclm.xsap.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.dto.CourseScheduleDTO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.mapper.TEmployeeMapper;
import com.kclm.xsap.mapper.TMemberCardMapper;
import com.kclm.xsap.mapper.TScheduleRecordMapper;
import com.kclm.xsap.service.CourseScheduleService;

public class CourseScheduleServiceImpl implements CourseScheduleService{

	@Autowired
	TScheduleRecordMapper scheduleMapper;
	
	@Override
	public boolean save(TScheduleRecord schedule) {
		scheduleMapper.insert(schedule);
		return true;
	}

	@Override
	public boolean deleteById(Long id) {
		scheduleMapper.deleteById(id);
		return true;
	}

	@Override
	public List<CourseScheduleDTO> listSchedule(LocalDate startDate, LocalDate endDate) {
		List<TScheduleRecord> scheduleList = scheduleMapper.selectList(new QueryWrapper<TScheduleRecord>()
				.between("start_date", startDate, endDate));
		List<CourseScheduleDTO> courseScheduleDtoList = new ArrayList<>();
		for(int i = 0; i < scheduleList.size(); i++) {
			Long id = scheduleList.get(i).getId();
			CourseScheduleDTO courseScheduleDTO = findById(id);
			courseScheduleDtoList.add(courseScheduleDTO);
		}
		return courseScheduleDtoList;
	}

	@Autowired
	TCourseMapper courseMapper;
	
	@Autowired
	TMemberCardMapper cardMapper;
	
	@Autowired
	TEmployeeMapper employeeMapper;
	
	@Override
	public CourseScheduleDTO findById(Long scheduleId) {
		CourseScheduleDTO scheduleDto = new CourseScheduleDTO();
		//获取当前选中的排课记录信息
		TScheduleRecord schedule = scheduleMapper.selectById(scheduleId);
		//获取上课时间
		LocalTime classTime = schedule.getClassTime();
		LocalDateTime startTime = LocalDateTime.of(schedule.getStartDate(),classTime);
		
		scheduleDto.setStartTime(startTime);
		scheduleDto.setLimitSex(schedule.getLimitSex());
		scheduleDto.setLimitAge(schedule.getLimitAge());
		
		//根据排课记录获取到对应的课程信息
		TCourse course = courseMapper.selectById(schedule.getCourseId());
		Integer duration = course.getDuration();
		LocalTime plusClassTime = classTime.plusMinutes(duration);
		LocalDateTime endTime = LocalDateTime.of(schedule.getStartDate(),plusClassTime);
		
		scheduleDto.setCourseName(course.getName());
		scheduleDto.setEndTime(endTime);
		scheduleDto.setDuration(duration);
		scheduleDto.setClassNumbers(course.getContains());
		
		//根据课程id获取到支持的会员卡信息
		/*
		 SELECT * FROM t_member_card WHERE id IN (SELECT card_id FROM t_course_card ca,
 			t_course co WHERE ca.course_id = co.id);
		 */
		List<TMemberCard> cardList = cardMapper.selectList(new QueryWrapper<TMemberCard>().inSql("id",
				"SELECT card_id FROM t_course_card ca,t_course co WHERE ca.course_id = co.id"));
		//拼接会员卡名
		StringBuilder sb = new StringBuilder();
		TMemberCard card;
		for(int i = 0; i < cardList.size() ; i++) {
			card = cardList.get(i);
			if(i < cardList.size() -1) {
				sb.append(card.getName() + " | ");
			}
			sb.append(card.getName());
		}
		scheduleDto.setSupportCard(sb.toString());
		
		//根据排课记录获取对应的老师信息
		String teacherName = employeeMapper.selectById(schedule.getTeacherId()).getName();
		
		scheduleDto.setTeacher(teacherName);
		
		//获取当前课程对应的预约记录
		ReserveServiceImpl reserveService = new ReserveServiceImpl();
		List<ReserveRecordDTO> reserveDTO = reserveService.listReserveRecords(scheduleId);
		scheduleDto.setReserveRecord(reserveDTO);
		
		return scheduleDto;
	}

}
