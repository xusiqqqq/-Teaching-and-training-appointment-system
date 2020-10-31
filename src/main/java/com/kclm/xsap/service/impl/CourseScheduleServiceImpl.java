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
import com.kclm.xsap.config.XsapConfig;
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

	//------------时间间隔-------------
	//private static  Long minute_set = 45L;
	//-------------------------

	@Autowired
	XsapConfig xsapConfig;

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
		LocalDate startDate = schedule.getStartDate();
		LocalTime classTime = schedule.getClassTime();
		//同一天内，同一位老师，距离上次上课结束45个分钟内，或当前课程时间与后面邻接时间有交叉，则该时间无效
		//查询结果按日期-时间“升序”排列
		List<TScheduleRecord> findList = scheduleMapper.selectList(new QueryWrapper<TScheduleRecord>()
				.eq("teacher_id",  schedule.getTeacherId()).eq("start_date", startDate).orderByAsc(true, "start_date","class_time"));
		//不是当天的课，首次填入值时就有效
		if(findList == null || findList.size() < 1) {
			System.out.println("nice_time----------" + classTime);
			scheduleMapper.insert(schedule);
			return true;
		}

		//数据库仅有一条记录时
		if(findList != null && findList.size() == 1) {
			//数据库仅有一条数据
			TScheduleRecord findOne = scheduleMapper.selectById(findList.get(0));
			LocalTime classTimeOne = findOne.getClassTime();
			//课程-前-指定分钟内不能新增排课
			System.out.println("************课程间隔: "+xsapConfig.getGap_minute());
			LocalTime preTimeOne = classTimeOne.minusMinutes(xsapConfig.getGap_minute());
			//课程-后-指定分钟内不能新增排课
			LocalTime postTimeOne = classTimeOne.plusMinutes(xsapConfig.getGap_minute());
			//同已有课程之后的时间段不冲突
			if(classTime.isAfter(postTimeOne)) {
				System.out.println("nice_time2----------" + classTime);
				scheduleMapper.insert(schedule);
				return true;
			}
			//同已有课程之前的时间段不冲突
			if(classTime.isBefore(preTimeOne)) {
				System.out.println("nice_time3----------" + classTime);
				scheduleMapper.insert(schedule);
				return true;
			}
			return false;
		}


		//判断当前时间是否有效。1，有效
		Integer flag = 0;
		for(int i = 0; i < findList.size() -1; i++) {
			TScheduleRecord findOne = scheduleMapper.selectById(findList.get(i));
			TScheduleRecord findNext = scheduleMapper.selectById(findList.get(i+1));
			LocalTime classTimeOne = findOne.getClassTime();
			LocalTime classTimeNext = findNext.getClassTime();
			//课程-前-指定分钟内不能新增排课
			LocalTime preTimeOne = classTimeOne.minusMinutes(xsapConfig.getGap_minute());
			//课程-后-指定分钟内不能新增排课
			LocalTime postTimeOne = classTimeOne.plusMinutes(xsapConfig.getGap_minute());
			LocalTime preTimeNext = classTimeNext.minusMinutes(xsapConfig.getGap_minute());
			LocalTime postTimeNext = classTimeNext.plusMinutes(xsapConfig.getGap_minute());

			//若当前时间在首个时间段之前，有效
			if(i == 0 && classTime.isBefore(preTimeOne)) {
				System.out.println("---" + classTime + " 在 " +  classTimeOne +" 之前有效");
				//表明输入的时间有效
				flag = 1;
				break;
			}

			//若当前时间在前面时间段之后，在后面时间段之前，则有效
			if(classTime.isAfter(postTimeOne) && classTime.isBefore(preTimeNext)) {
				System.out.println("------perfect------");
				System.out.println("---" + classTime + " 在 " + classTimeOne +" ~ " + classTimeNext +" 之间有效");
				System.out.println("-------===========------");
				//表明输入的时间有效
				flag = 1;
				break;
			}

			//若当前时间在最后一个时间段里面
			if(i == findList.size() - 2 && classTime.isAfter(postTimeNext)) {
				System.out.println("---" + classTime + " 在 " + classTimeNext +" 之后有效");
				//表明输入的时间有效
				flag = 1;
				break;
			}
		}

		//当前时间合适，允许填入数据库
		if(flag == 1) {
			System.out.println("nice_time----------" + classTime);
			scheduleMapper.insert(schedule);
			return true;
		}else {
			System.out.println("bad_time------距离当前时间"+ xsapConfig.getGap_minute() +"分钟内已有课程安排----" + classTime);
			return false;
		}

	}

	@Override
	public boolean deleteById(Long id) {
		TScheduleRecord scheduleRecord = scheduleMapper.selectById(id);
		if(scheduleRecord == null) {
			System.out.println("-------无此条排课记录");
			return false;
		}

		List<TReservationRecord> reservationRecord = reserveMapper.selectList(new QueryWrapper<TReservationRecord>().eq("schedule_id", id));
		List<TClassRecord> classRecord = classMapper.selectList(new QueryWrapper<TClassRecord>().eq("schedule_id", id));
		//对应的预约记录或上课记录存在时
		if(reservationRecord.size() > 0 || classRecord.size() > 0) {
			return false;
		}
		/*----------不进行关联删除----------*/
		//删除排课对应的预约记录
//		reserveMapper.delete(new QueryWrapper<TReservationRecord>().eq("schedule_id", id));
		//删除排课对应的上课记录
//		classMapper.delete(new QueryWrapper<TClassRecord>().eq("schedule_id", id));

		scheduleMapper.deleteById(id);
		return true;
	}

	//所有的排课记录
	@Override
	public List<CourseScheduleDTO> listScheduleAll() {
		List<CourseScheduleDTO> listScheduleView = scheduleMapper.listScheduleView();
		for (CourseScheduleDTO courseSchedule : listScheduleView) {
			//开始上课时间
			courseSchedule.setStartTime(LocalDateTime.of(courseSchedule.getClassDate(),courseSchedule.getClassTime()));
			//下课时间
			courseSchedule.setEndTime(courseSchedule.getStartTime().plusMinutes(courseSchedule.getDuration()));
		}
		return listScheduleView;
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
		if(scheduleDto != null) {
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
		}

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
