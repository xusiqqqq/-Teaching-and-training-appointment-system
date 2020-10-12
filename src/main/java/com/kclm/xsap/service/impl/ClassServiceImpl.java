package com.kclm.xsap.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.entity.TClassRecord;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TClassRecordMapper;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.mapper.TReservationRecordMapper;
import com.kclm.xsap.mapper.TScheduleRecordMapper;
import com.kclm.xsap.service.ClassService;

@Service
@Transactional
public class ClassServiceImpl implements ClassService{

	@Autowired
	TReservationRecordMapper reserveMapper;
	
	@Autowired
	TScheduleRecordMapper scheduleMapper;
	
	@Autowired
	TCourseMapper courseMapper;
	
	@Autowired
	TClassRecordMapper classMapper;
	
	@Override
	public boolean save() {
		//找出用户的预约记录，若其状态为“已预约”，则录入上课记录
		List<TReservationRecord> reserveList = reserveMapper.selectList(
				new QueryWrapper<TReservationRecord>().eq("status", 1));
		if(reserveList == null || reserveList.size() < 1) {
			System.out.println("无任何预约记录，无法生成上课记录。。。");
			return false;
		}
		for (TReservationRecord reserve : reserveList) {
			TClassRecord classed = new TClassRecord();
			//存入会员id
			classed.setMemberId(reserve.getMemberId());
			//存入会员卡名
			classed.setCardName(reserve.getCardName());
			//存入排课计划id
			classed.setScheduleId(reserve.getScheduleId());
			//存入上课备注
			classed.setNote(reserve.getClassNote());
			//存入教师评语
			classed.setComment(reserve.getComment());
			//课程结束后，上课结束时间作为上课记录的创建时间
			TScheduleRecord schedule = scheduleMapper.selectById(reserve.getScheduleId());
			TCourse course = courseMapper.selectById(schedule.getCourseId());
			LocalTime plusClassTime = schedule.getClassTime().plusMinutes(course.getDuration());
			LocalDateTime endTime = LocalDateTime.of(schedule.getStartDate(),plusClassTime);
			classed.setCreateTime(endTime);
			//录入一条上课记录
			classMapper.insert(classed);
		}
		
		return false;
	}

	@Override
	public boolean update(TClassRecord classed) {
		classMapper.updateById(classed);
		return true;
	}

}
