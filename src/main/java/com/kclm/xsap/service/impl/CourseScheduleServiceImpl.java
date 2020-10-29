package com.kclm.xsap.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.CourseScheduleDTO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.dto.convert.ClassRecordConvert;
import com.kclm.xsap.dto.convert.CourseScheduleConvert;
import com.kclm.xsap.entity.TClassRecord;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TClassRecordMapper;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.mapper.TEmployeeMapper;
import com.kclm.xsap.mapper.TMemberCardMapper;
import com.kclm.xsap.mapper.TMemberMapper;
import com.kclm.xsap.mapper.TReservationRecordMapper;
import com.kclm.xsap.mapper.TScheduleRecordMapper;
import com.kclm.xsap.service.CourseScheduleService;

@Service
@Transactional
@Slf4j
public class CourseScheduleServiceImpl implements CourseScheduleService{

//	@Autowired
//	private CourseScheduleConvert courseScheduleConvert;
	
//	@Autowired
//	ClassRecordConvert classRecordConvert;

	@Autowired
	TScheduleRecordMapper scheduleMapper;
	
	@Autowired
	TReservationRecordMapper reserveMapper;
	
	@Autowired
	TCourseMapper courseMapper;
	
	@Autowired
	TMemberCardMapper cardMapper;
	
	@Autowired
	TEmployeeMapper employeeMapper;
	
	@Autowired
	TClassRecordMapper classMapper;
	
	@Autowired
	TMemberMapper memberMapper;
	
	@Autowired
	ReserveServiceImpl reserveService;
	
	@Override
	public boolean save(TScheduleRecord schedule) {
		Long courseId = schedule.getCourseId();
		Long teacherId = schedule.getTeacherId();
		LocalDate startDate = schedule.getStartDate();
		LocalTime classTime = schedule.getClassTime();
		//同一天内，同一门课，同一位老师，距离上次上课结束1个小时内，不可二次排课
		TScheduleRecord findOne = scheduleMapper.selectOne(new QueryWrapper<TScheduleRecord>()
				.eq("course_id", courseId).eq("teacher_id", teacherId).eq("start_date", startDate));
		if(findOne != null) {
			//若距离上次上课结束1个小时内
			if(findOne.getClassTime() != null) {
				LocalTime inOneHour = findOne.getClassTime().plusHours(1L);
				if(classTime.isBefore(inOneHour)){
					System.out.println("相同一堂课不能在一小时内排两次！");
					return false;
				}				
			}else {
				System.out.println("此堂课没有具体的上课时间，排课无效！");
				return false;				
			}
		}
		
		scheduleMapper.insert(schedule);
		return true;
	}

	@Override
	public boolean deleteById(Long id) {
		TScheduleRecord scheduleRecord = scheduleMapper.selectById(id);
		if(scheduleRecord == null) {
			System.out.println("-------无此条排课记录");
			return false;
		}
		//删除排课对应的预约记录
		reserveMapper.delete(new QueryWrapper<TReservationRecord>().eq("schedule_id", id));
		//删除排课对应的上课记录
		classMapper.delete(new QueryWrapper<TClassRecord>().eq("schedule_id", id));
		
		scheduleMapper.deleteById(id);
		return true;
	}

	//所有的排课记录
	@Override
	public List<CourseScheduleDTO> listScheduleAll() {
		List<TScheduleRecord> scheduleList = scheduleMapper.selectList(null);
		List<CourseScheduleDTO> courseScheduleDtoList = new ArrayList<>();
		for(int i = 0; i < scheduleList.size(); i++) {
			Long id = scheduleList.get(i).getId();
			CourseScheduleDTO courseScheduleDTO = findById(id);
			courseScheduleDtoList.add(courseScheduleDTO);
		}
		return courseScheduleDtoList;
	}
	
	//获取给定的日期范围内所有的排课记录
	@Override
	public List<CourseScheduleDTO> listSchedule(LocalDate startDate, LocalDate endDate) {
		if(startDate == null || endDate == null) {
			System.out.println("------输入参数不全");
			return null;
		}
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

	@Override
	public CourseScheduleDTO findById(Long scheduleId) {
		//获取当前选中的排课记录信息
		
		//==获取当前课程的上课数据（已预约时）		
		List<ClassRecordDTO> classDtoList = scheduleMapper.listClassView(scheduleId);
		for (ClassRecordDTO classDto : classDtoList) {
			BigDecimal price = new BigDecimal(classDto.getPrice().toString());
			BigDecimal count = new BigDecimal(classDto.getCount().toString());
			BigDecimal unitPrice = price.divide(count, 2, RoundingMode.HALF_UP);	
			//消耗的次数
			BigDecimal countCost = new BigDecimal(classDto.getTimesCost().toString());
			BigDecimal involveMoney = unitPrice.multiply(countCost);
			classDto.setInvolveMoney(involveMoney);
		}
		
		//已预约记录
		List<ReserveRecordDTO> reservedList = reserveMapper.listReservedView(scheduleId);
		//预约记录
		List<ReserveRecordDTO> reserveDtoList = reserveMapper.listReserveRecordView(scheduleId);
		
		CourseScheduleDTO scheduleDto = scheduleMapper.oneScheduleView(scheduleId);
		//没有会员卡支持时
		if(scheduleDto == null) {
			scheduleDto = scheduleMapper.oneScheduleNoCardView(scheduleId);
		}
		//开始上课时间
		scheduleDto.setStartTime(LocalDateTime.of(scheduleDto.getClassDate(),scheduleDto.getClassTime()));
		//下课时间
		scheduleDto.setEndTime(scheduleDto.getStartTime().plusMinutes(scheduleDto.getDuration()));

		//已预约记录
		scheduleDto.setReservedList(reservedList);
		//	预约记录
		scheduleDto.setReserveRecord(reserveDtoList);
		//	上课数据
		scheduleDto.setClassRecord(classDtoList);
		
		return scheduleDto;
	}

	//复制排课
	@Override
	public boolean copySchedule(LocalDate sourceDate, LocalDate targetDate) {
		if(sourceDate == null || targetDate == null) {
			System.out.println("------输入参数不全");
			return false;
		}
		
		 List<TScheduleRecord> sourceList = scheduleMapper.selectList(new QueryWrapper<TScheduleRecord>()
				.eq("start_date", sourceDate));
		if(sourceList == null || sourceList.size() < 1) {
			log.debug("--------当前日程无排课计划");
			return false;
		}
		
		//若目标日期在源日期或之前，非法
		if(sourceDate.isEqual(targetDate) || sourceDate.isAfter(targetDate)) {
			log.debug("--------日程范围错误");
			return false;
		}
		List<TScheduleRecord> targetList = scheduleMapper.selectList(new QueryWrapper<TScheduleRecord>()
				.eq("start_date", targetDate));
		if(targetList.size() > 0) {
			log.debug("--------目标日程已经排好课了");
			return false;
		}
		for (TScheduleRecord source : sourceList) {
			//对预约数清零
			source.setOrderNums(0);
			source.setStartDate(targetDate);
			//创建时间
			source.setCreateTime(LocalDateTime.now());
			//数据变动版本
			source.setVersion(1);
			scheduleMapper.insert(source);			
		}
		return true;
	}

}
