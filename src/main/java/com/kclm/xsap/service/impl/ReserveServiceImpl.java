package com.kclm.xsap.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.dto.convert.ReserveRecordConvert;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TGlobalReservationSet;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TClassRecordMapper;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.mapper.TGlobalReservationSetMapper;
import com.kclm.xsap.mapper.TMemberMapper;
import com.kclm.xsap.mapper.TReservationRecordMapper;
import com.kclm.xsap.mapper.TScheduleRecordMapper;
import com.kclm.xsap.service.ReserveService;

@Service
@Transactional
public class ReserveServiceImpl implements ReserveService{

//	@Autowired
//	private ReserveRecordConvert reserveRecordConvert;

	@Autowired
	TReservationRecordMapper reserveMapper;
	
	@Autowired
	TScheduleRecordMapper scheduleMapper;
	
	@Autowired
	TCourseMapper courseMapper;
	
	@Autowired
	TMemberMapper memberMapper;
	
	@Autowired
	TClassRecordMapper classMapper;
	
	@Autowired
	TGlobalReservationSetMapper globalMapper;
	
	//查询一条预约记录
	@Override
	public TReservationRecord findOne(Long reserveId) {
		TReservationRecord reservationRecord = reserveMapper.selectById(reserveId);
		return reservationRecord;
	}
	
	/*----判断预约是否符合全局预约规定----*/
	private Integer reserveCheck(TReservationRecord reserve) {
		
		//判断合法日期
		LocalDate checkStartDate = null;
		LocalDate checkEndDate = null;
		LocalDate checkCancelDate = null;
		
		//组合时间
		LocalDateTime checkStart = null;
		LocalDateTime checkEnd =null;
		LocalDateTime checkCancel =null;
		
		TScheduleRecord findOne = scheduleMapper.selectById(reserve.getScheduleId());
		LocalDate startDate = findOne.getStartDate();
		LocalTime classTime = findOne.getClassTime();
		
		TGlobalReservationSet globalSet = globalMapper.selectById(1);
		//预约开始时间（距离开课前的天数） -	根据创建时间来判断
		if(globalSet.getStartDay() > 0) {
			checkStartDate = startDate.minusDays(globalSet.getStartDay());
			checkStart = LocalDateTime.of(checkStartDate, classTime);
		}
		
		//有开始预约限制
		if(checkStartDate != null) {
			//若早于允许的提前预约时间，则预约无效
			if(reserve.getCreateTime().isBefore(checkStart)) {
				return 1;
			}
		}
		/*------------off------------*/
		
		//预约截止时间	:	根据创建时间来判断
		if(globalSet.getEndDay() > 0) {
			checkEndDate = startDate.minusDays(globalSet.getEndDay());
			checkEnd = LocalDateTime.of(checkEndDate, globalSet.getEndTime());
		}else if(globalSet.getEndHour() > 0) {
			checkEndDate = startDate;	//仅判断用
			checkEnd = LocalDateTime.of(startDate, classTime.minusHours(globalSet.getEndHour()));
		}
		
		//有预约截止时间限制
		if(checkEndDate != null) {
			//若晚于预约截止时间，预约无效
			if(reserve.getCreateTime().isAfter(checkEnd)) {
				return 2;
			}
		}
		
		//预约取消时间 : 根据修改时间来判断
		if(globalSet.getCancelDay() > 0) {
			checkCancelDate = startDate.minusDays(globalSet.getCancelDay());
			checkCancel = LocalDateTime.of(checkCancelDate, globalSet.getCancelTime());
		}else if(globalSet.getCancelHour() > 0) {
			checkCancelDate = startDate;	//仅判断用
			checkCancel = LocalDateTime.of(startDate, classTime).minusHours(globalSet.getCancelHour());
		}
		
		//有预约取消限制
		if(checkCancelDate != null) {
			//若取消时间晚于可取消时间，取消无效
			if(reserve.getLastModifyTime().isAfter(checkCancel)) {
				return 3;
			}
		}
		
		return 0;
	}
	
	
	//新增预约记录
	@Override
	public Integer save(TReservationRecord reserve) {
		//判断：用户是否已经用任意一张会员卡预约过某次排课计划
		//检查状态
		Integer check = reserveCheck(reserve);
		//预约时间过早
		if(check == 1) {
			System.out.println("预约时间过早");
			return 1;
		}
		//预约时间过晚
		if(check == 2) {
			System.out.println("预约时间过晚");
			return 2;
		}
		
		TScheduleRecord schedule = scheduleMapper.selectById(reserve.getScheduleId());
		Integer contains = courseMapper.selectById(schedule.getCourseId()).getContains();
		Integer totalNums = schedule.getOrderNums() + reserve.getReserveNums();
		if(totalNums >= contains ) {
			System.out.println("当前课程预约人数：" + schedule.getOrderNums());
			System.out.println("人数超额！" + (totalNums - contains) + " 人 ");
			return -2;
		}
		//预约人数增加
		schedule.setOrderNums(totalNums);
		scheduleMapper.updateById(schedule);
		
		//判断新增的预约曾经是否预约过
		TReservationRecord checkResult = reserveMapper.selectOne(new QueryWrapper<TReservationRecord>()
				.eq("member_id", reserve.getMemberId()).eq("schedule_id", reserve.getScheduleId()).eq("id", reserve.getId()));
		//若新预约的课，曾经预约过，则对其进行更新
		if(checkResult != null) {
			if(checkResult.getStatus() == 1) {
				System.out.println("已经用过会员卡预约过当前课程，无法再次预约！");
				return -1;
			}
			reserve.setStatus(1);		
			reserveMapper.updateById(reserve);
			return 0;
		}
		
		//未预约，添加会员预约时，则设置状态为已预约
		reserve.setStatus(1);		
		reserveMapper.insert(reserve);
		return 0;
	}
	
	//更新预约记录
	@Override
	public Integer update(TReservationRecord reserve) {
		//检查状态
		Integer check = reserveCheck(reserve);
		if(check == 3) {
			System.out.println("预约取消时间过晚");
			return 3;
		}
		
		TScheduleRecord schedule = scheduleMapper.selectById(reserve.getScheduleId());
		//统计预约取消次数
		TReservationRecord cancelled = reserveMapper.selectOne(new QueryWrapper<TReservationRecord>()
				.eq("status", 0).eq("id", reserve.getId()));
		if(cancelled != null) {
			reserve.setCancelTimes(reserve.getCancelTimes() + 1);
			System.out.println("-------预约取消次数："+reserve.getCancelTimes());
			reserveMapper.updateById(reserve);
		}
		//判断当卡用户对当前课程预约的次数
		Integer limitCounts = courseMapper.selectById(schedule.getCourseId()).getLimitCounts();
		if(reserve.getCancelTimes() >= limitCounts) {
			System.out.println("当前课程预约次数已满，不可再进行预约");
			return -1;
		}
		//预约人数判断
		Integer contains = courseMapper.selectById(schedule.getCourseId()).getContains();
		Integer totalNums = schedule.getOrderNums() + reserve.getReserveNums();
		if(totalNums >= contains ) {
			System.out.println("当前课程预约人数：" + schedule.getOrderNums());
			System.out.println("人数超额！" + (totalNums - contains) + " 人 ");
			return -2;
		}
		//预约人数变动
		if(reserve.getStatus() == 1) {
			schedule.setOrderNums(totalNums);			
		}else {
			schedule.setOrderNums(schedule.getOrderNums() - reserve.getReserveNums());		
		}
		scheduleMapper.updateById(schedule);
		//预约人数满足条件时，进行更新
		reserveMapper.updateById(reserve);
		return 0;
	}
	
	//当前排课的已预约记录
	@Override
	public List<ReserveRecordDTO> listReserved(Long scheduleId) {
		//1、获取到排课信息
		TScheduleRecord schedule = scheduleMapper.selectById(scheduleId);
		if(schedule == null) {
			System.out.println("--------此排课信息不存在");
			return null;
		}
		//2、根据排课计划id获取到对应的课程信息
		TCourse course = courseMapper.selectById(schedule.getCourseId());
		//3、根据scheduleId查询到对应的已预约记录
		List<TReservationRecord> reserveList = reserveMapper.selectList(
				new QueryWrapper<TReservationRecord>().eq("schedule_id", scheduleId).eq("status", 1));
		
		//不存在已预约记录
		if(reserveList == null  || reserveList.size() < 1) {
			return null;
		}
		
		TReservationRecord reserve = null;
		//4、根据预约记录获取会员信息
		List<TMember> memberList = new ArrayList<TMember>();
		for (int i = 0; i < reserveList.size(); i++) {
			reserve = reserveList.get(i);
			TMember member = memberMapper.selectById(reserveList.get(i).getMemberId());
			memberList.add(member);
		}
		
		//5、组合成DTO数据信息
		List<ReserveRecordDTO> reserveDtoList = new ArrayList<>();
		if(reserve != null) {
			//sql结果对应关系
			//1个  排课号 =》 1门课（由会员指定1张会员卡） =》  n个  预约记录（1条 预约记录 =》 1个 会员号）
			System.out.println("-----------");
			System.out.println("会员列表条目："+memberList.size());
			System.out.println("-----------");
			for(int i = 0; i < memberList.size(); i++) {
				TMember member = new TMember();
				member = memberList.get(i);
				//========DTO存储
				ReserveRecordDTO reserveRecordDTO = new ReserveRecordDTO();
				reserveRecordDTO.setReserveId(reserve.getId());
				reserveRecordDTO.setMemberName(member.getName());
				reserveRecordDTO.setPhone(member.getPhone());
				reserveRecordDTO.setCardName(reserve.getCardName());
				reserveRecordDTO.setReserveNumbers(schedule.getOrderNums());
				reserveRecordDTO.setTimesCost(course.getTimesCost());
				reserveRecordDTO.setOperateTime(reserve.getCreateTime());
				reserveRecordDTO.setOperator(reserve.getOperator());
				reserveRecordDTO.setReserveNote(reserve.getNote());
				//添加到预约记录集合
				reserveDtoList.add(reserveRecordDTO);
			}
		}
		
		return reserveDtoList;
	}
	
	//当前排课的预约记录
	@Override
	public List<ReserveRecordDTO> listReserveRecords(Long scheduleId) {
		//1、获取到排课信息
		TScheduleRecord schedule = scheduleMapper.selectById(scheduleId);
		if(schedule == null) {
			return null;
		}
		//2、根据排课计划id获取到对应的课程信息
		TCourse course = courseMapper.selectById(schedule.getCourseId());
		//3、根据scheduleId查询到对应的预约记录
		List<TReservationRecord> reserveList = reserveMapper.selectList(
				new QueryWrapper<TReservationRecord>().eq("schedule_id", scheduleId));
		TReservationRecord reserve = null;
		
		//不存在预约记录
		if(reserveList == null || reserveList.size() < 1) {
			return null;
		}
		
		//4、根据预约记录获取会员信息
		List<TMember> memberList = new ArrayList<TMember>();
		for (int i = 0; i < reserveList.size(); i++) {
			reserve = reserveList.get(i);
			TMember member = memberMapper.selectById(reserveList.get(i).getMemberId());
			memberList.add(member);
		}
		
		//5、组合成DTO数据信息
		List<ReserveRecordDTO> reserveDtoList = new ArrayList<>();
		if(reserve != null) {
			//sql结果对应关系
			//1个  排课号 =》 1门课（由会员指定1张会员卡） =》  n个  预约记录（1条 预约记录 =》 1个 会员号）
			System.out.println("-----------");
			System.out.println("会员列表条目："+memberList.size());
			System.out.println("-----------");
			for(int i = 0; i < memberList.size(); i++) {
				TMember member = new TMember();
				member = memberList.get(i);
				//========DTO存储
				ReserveRecordDTO reserveRecordDTO = new ReserveRecordDTO();
				reserveRecordDTO.setReserveId(reserve.getId());
				reserveRecordDTO.setMemberName(member.getName());
				reserveRecordDTO.setPhone(member.getPhone());
				reserveRecordDTO.setCardName(reserve.getCardName());
				reserveRecordDTO.setReserveNumbers(schedule.getOrderNums());
				reserveRecordDTO.setTimesCost(course.getTimesCost());
				reserveRecordDTO.setOperateTime(reserve.getCreateTime());
				reserveRecordDTO.setOperator(reserve.getOperator());
				reserveRecordDTO.setReserveNote(reserve.getNote());
				reserveRecordDTO.setReserveStatus(reserve.getStatus());
				//添加到预约记录集合
				reserveDtoList.add(reserveRecordDTO);
			}
		}
		
		return reserveDtoList;
	}

	//导出当前排课的预约记录
	@Override
	public List<ReserveRecordDTO> listExportRecord(Long scheduleId) {
		//数据由上面的方法查询到
		List<ReserveRecordDTO> reserveRecordDto = listReserveRecords(scheduleId);
		//POI 
		return reserveRecordDto;
	}

	//导出指定时间段的预约记录
	@Override
	public List<ReserveRecordDTO> listExportRecordRange(LocalDate startDate, LocalDate endDate) {
		//对日期区间做LocalDateTime类型转换
		LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
		LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
		//查找日期区间的预约记录
		List<TReservationRecord> reserveList = reserveMapper.selectList(new QueryWrapper<TReservationRecord>()
				.between("create_time", startDateTime, endDateTime));
		List<ReserveRecordDTO> reserveDtoList = new ArrayList<>();
		if(reserveList == null) {
			System.out.println("-------该段时间内，没有任何预约记录");
			return null;
		}
		
		for(int i = 0; i < reserveList.size(); i++) {
			Long id = reserveList.get(i).getScheduleId();
			List<ReserveRecordDTO> listReserveRecordsDto = listReserveRecords(id);
			if(listReserveRecordsDto != null)
				reserveDtoList.addAll(listReserveRecordsDto);
		}
		
		return reserveDtoList;
	}

}
