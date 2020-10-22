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
			}
			System.out.println("此堂课没有具体的上课时间，排课无效！");
			return false;
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
		TScheduleRecord schedule = scheduleMapper.selectById(scheduleId);
		if(schedule == null) {
			return null;
		}
		//根据排课记录获取到对应的课程信息
		TCourse course = courseMapper.selectById(schedule.getCourseId());
		
		//根据课程id获取到支持的会员卡信息
		List<TMemberCard> cardList = cardMapper.selectList(new QueryWrapper<TMemberCard>().inSql("id",
				"SELECT card_id FROM t_course_card WHERE course_id = " + course.getId()));
		//拼接会员卡名
		StringBuilder sb = new StringBuilder();
		TMemberCard card = new TMemberCard();
		for(int i = 0; i < cardList.size() ; i++) {
			card = cardList.get(i);
			if(i>= 1 && i < cardList.size()) {
				sb.append(" | ");
			}
			sb.append(card.getName());
		}
		String supportCards = sb.toString();
		
		//根据排课记录获取对应的老师信息
		String teacherName = employeeMapper.selectById(schedule.getTeacherId()).getName();
		
		//获取当前课程对应的预约记录
		List<ReserveRecordDTO> reserveDtoList = reserveService.listReserveRecords(scheduleId);
		System.out.println("-------reserveDTO: "+reserveDtoList);
		//获取当前课程对应的  已预约记录
		List<ReserveRecordDTO> reservedList = reserveService.listReserved(scheduleId);
		System.out.println("-------reservedDTO: "+reservedList);
		
		//==获取当前课程的上课数据
		List<TClassRecord> classList = classMapper.selectList(new QueryWrapper<TClassRecord>().eq("schedule_id", scheduleId));
		
		List<ClassRecordDTO> classDtoList = new ArrayList<ClassRecordDTO>();
		if(classList != null && classList.size() > 0) {
			for (TClassRecord classed : classList) {
				//查出会员卡的次数单价，取值四舍五入
				TMemberCard memberCard = cardMapper.selectOne(new QueryWrapper<TMemberCard>()
						.eq("name", classed.getCardName()));
				if(memberCard == null) {
					System.out.println("没有此会员卡！");
					return null;
				}
				BigDecimal price = new BigDecimal(memberCard.getPrice().toString());
				BigDecimal count = new BigDecimal(memberCard.getTotalCount().toString());
				BigDecimal unitPrice = price.divide(count, 2, RoundingMode.HALF_UP);	
				//消耗的次数
				BigDecimal countCost = new BigDecimal(course.getTimesCost().toString());
				BigDecimal involveMoney = unitPrice.multiply(countCost);
				//获取会员信息
				TMember member = memberMapper.selectById(classed.getMemberId());
				//====== dto值存储
				ClassRecordDTO classRecordDTO = new ClassRecordDTO();
				//上课记录id
				classRecordDTO.setClassRecordId(classed.getId());
				//	会员信息
				classRecordDTO.setMember(member);
				//	会员卡名
				classRecordDTO.setCardName(classed.getCardName());
				//	消费次数
				classRecordDTO.setTimesCost(course.getTimesCost());
				//	消费金额
				classRecordDTO.setInvolveMoney(involveMoney);
				//	操作时间
				classRecordDTO.setOperateTime(classed.getCreateTime());
				System.out.println("-----上课记录："+classRecordDTO);
				System.out.println("------------------");
				//dto拼接
				classDtoList.add(classRecordDTO);
			}
		}
		//======dto值存储
		CourseScheduleDTO scheduleDto = new CourseScheduleDTO();
		//排课记录id
		scheduleDto.setScheduleId(scheduleId);
		//	课程名
		scheduleDto.setCourseName(course.getName());
		//	上课时间
		scheduleDto.setStartTime(LocalDateTime.of(schedule.getStartDate(),schedule.getClassTime()));
		//	计算下课时间
		LocalTime plusClassTime = null;
		if(schedule.getClassTime() != null) {			
			plusClassTime = schedule.getClassTime().plusMinutes(course.getDuration());
		}
		LocalDateTime endTime = LocalDateTime.of(schedule.getStartDate(),plusClassTime);
		scheduleDto.setEndTime(endTime);
		//	时长
		scheduleDto.setDuration(course.getDuration());
		//	限制性别
		scheduleDto.setLimitSex(course.getLimitSex());
		//	限制年龄
		scheduleDto.setLimitAge(course.getLimitAge());
		//	支持的会员卡
		scheduleDto.setSupportCards(supportCards);
		//	上课老师
		scheduleDto.setTeacherName(teacherName);
		//	上课容纳人数
		scheduleDto.setClassNumbers(course.getContains());
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
