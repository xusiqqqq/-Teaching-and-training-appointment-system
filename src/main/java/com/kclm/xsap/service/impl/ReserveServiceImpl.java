package com.kclm.xsap.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.dto.convert.ReserveRecordConvert;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.mapper.TMemberMapper;
import com.kclm.xsap.mapper.TReservationRecordMapper;
import com.kclm.xsap.mapper.TScheduleRecordMapper;
import com.kclm.xsap.service.ReserveService;

public class ReserveServiceImpl implements ReserveService{

	@Autowired
	TReservationRecordMapper reserveMapper;
	
	@Autowired
	TScheduleRecordMapper scheduleMapper;
	
	@Autowired
	TCourseMapper courseMapper;
	
	@Autowired
	TMemberMapper memberMapper;
	
	@Override
	public boolean save(TReservationRecord reserve) {
		//判断：用户是否已经用任意一张会员卡预约过某次排课计划
		Map<String,Long> params = new HashMap<String, Long>();
		params.put("member_id", reserve.getMemberId());
		params.put("schedule_id", reserve.getScheduleId());
		TReservationRecord checkResult = reserveMapper.selectOne(new QueryWrapper<TReservationRecord>()
				.allEq(params));
		if(checkResult.getStatus() == 1) {
			System.out.println("已经用会员卡预约过当前课程，无法再次预约！");
			return false;
		}
		//未预约，添加会员预约时，设置状态为已预约
		reserve.setStatus(1);
		reserveMapper.insert(reserve);
		return true;
	}

	@Override
	public List<ReserveRecordDTO> listReserveRecords(Long scheduleId) {
		//1、获取到排课信息
		TScheduleRecord schedule = scheduleMapper.selectById(scheduleId);
		//2、根据排课计划id获取到对应的课程信息
		TCourse course = courseMapper.selectById(schedule.getCourseId());
		//3、根据scheduleId查询到对应的预约记录
		List<TReservationRecord> reserveList = reserveMapper.selectList(
				new QueryWrapper<TReservationRecord>().eq("schedule_id", scheduleId));
		//4、根据预约记录获取会员信息
		List<Long> idList = new ArrayList<>();
		for (int i = 0; i < reserveList.size(); i++) {
			 idList.add(reserveList.get(i).getMemberId());
		}
		List<TMember> memberList = memberMapper.selectBatchIds(idList);
		
		//5、组合成DTO数据信息
		//sql结果对应关系
		//1个  排课号 =》 1门课（由会员指定1张会员卡） =》  n个  预约记录（1条 预约记录 =》 1个 会员号）
		TReservationRecord reserve;
		TMember member;
		List<ReserveRecordDTO> reserveDtoList = new ArrayList<>();
		for(int i = 0; i < memberList.size(); i++) {
			member = memberList.get(i);
			for(int j = 0; j < reserveList.size(); j++) {
				reserve = reserveList.get(j);
				//DTO转换
				ReserveRecordDTO reserveRecordDTO = ReserveRecordConvert.INSTANCE.entity2Dto(course, schedule, member, reserve);	
				//添加到预约记录集合
				reserveDtoList.add(reserveRecordDTO);
			}
		}
		
		return reserveDtoList;
	}

	@Override
	public List<ReserveRecordDTO> listExportRecord(Long scheduleId) {
		//数据由上面的方法查询到
		List<ReserveRecordDTO> reserveRecordDto = listReserveRecords(scheduleId);
		return reserveRecordDto;
	}

	@Override
	public List<ReserveRecordDTO> listExportRecordRange(LocalDate startDate, LocalDate endDate) {
		//对日期区间做LocalDateTime类型转换
		LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
		LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
		//查找日期区间的预约记录
		List<TReservationRecord> reserveList = reserveMapper.selectList(new QueryWrapper<TReservationRecord>()
				.between("create_time", startDateTime, endDateTime));
		List<ReserveRecordDTO> reserveDtoList = new ArrayList<>();
		for(int i = 0; i < reserveList.size(); i++) {
			Long id = reserveList.get(i).getScheduleId();
			List<ReserveRecordDTO> listReserveRecordsDto = listReserveRecords(id);
			reserveDtoList.addAll(listReserveRecordsDto);
		}
		
		return reserveDtoList;
	}

}
