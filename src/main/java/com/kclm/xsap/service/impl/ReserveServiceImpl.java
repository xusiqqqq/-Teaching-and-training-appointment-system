package com.kclm.xsap.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TGlobalReservationSet;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TClassRecordMapper;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.mapper.TGlobalReservationSetMapper;
import com.kclm.xsap.mapper.TMemberBindRecordMapper;
import com.kclm.xsap.mapper.TMemberMapper;
import com.kclm.xsap.mapper.TReservationRecordMapper;
import com.kclm.xsap.mapper.TScheduleRecordMapper;
import com.kclm.xsap.service.ClassService;
import com.kclm.xsap.service.MemberCardService;
import com.kclm.xsap.service.ReserveService;

@Service
@Transactional
public class ReserveServiceImpl implements ReserveService{

//	@Autowired
//	private ReserveRecordConvert reserveRecordConvert;

	//=====全局变量=====
	private static final String reserve_source =  "店员预约";
	
	//===============
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
	
	@Autowired
	TMemberBindRecordMapper bindMapper;
	
	@Autowired
	ClassService classService;
	
	@Autowired
	MemberCardService cardService;
	
	//查询一条预约记录
	@Override
	public TReservationRecord findOne(Long reserveId) {
		TReservationRecord reservationRecord = reserveMapper.selectById(reserveId);
		return reservationRecord;
	}
	
	/*----判断预约是否符合全局预约规定----*/
	@Override
	public Integer reserveCheck(TReservationRecord reserve) {
		
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
		
		//最晚预约时间
		checkEnd = LocalDateTime.of(startDate, classTime);
		
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
		}else {
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
	/*------------off------------*/
	
	
	//新增预约记录
	@Override
	public Integer save(TReservationRecord reserve) {
		//判断：用户是否已经用任意一张会员卡预约过某次排课计划
		if(reserve == null) {
			return -3;
		}
		//预约来源
		reserve.setComment(reserve_source);
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
		
		//查看卡有没有过期
		TMemberCard card = cardService.findByName(reserve.getCardName());
		TMemberBindRecord bind = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>()
				.eq("member_id", reserve.getMemberId()).eq("card_id", card.getId()));
		if(card.getType() != "次卡(无期限)") {
			LocalDateTime createTime = bind.getCreateTime();
			LocalDateTime	endTime = null;					
			if(createTime !=null) {
				endTime = createTime.plusDays(bind.getValidDay());			
			}
			if(endTime.isBefore(LocalDateTime.now())) {
				System.out.println("当前卡已过期！");
				return 3;
			}
		}
		
		TScheduleRecord schedule = scheduleMapper.selectById(reserve.getScheduleId());
		/*	
		 * 对于年龄、性别的预约限制，暂且不写
		 * */
		if(schedule != null) {
			Integer contains = courseMapper.selectById(schedule.getCourseId()).getContains();
			Integer totalNums = schedule.getOrderNums() + reserve.getReserveNums();
			if(totalNums > contains ) {
				System.out.println("当前课程预约人数：" + schedule.getOrderNums());
				System.out.println("人数超额！" + (totalNums - contains) + " 人 ");
				return -2;
			}
			//预约人数增加
			schedule.setOrderNums(totalNums);			
		}
		
		
		//判断新增的预约曾经是否预约过
		TReservationRecord checkResult = reserveMapper.selectOne(new QueryWrapper<TReservationRecord>()
				.eq("member_id", reserve.getMemberId()).eq("schedule_id", reserve.getScheduleId()));
		//若新预约的课，曾经预约过，则对其进行更新
		if(checkResult != null) {
			if(checkResult.getStatus() == 1) {
				System.out.println("已经用过会员卡预约过当前课程，无法再次预约！");
				return -1;
			}
			//排课更新，预约人数增加
			scheduleMapper.updateById(schedule);
			//同一堂课再次预约
			reserve.setId(checkResult.getId());
			reserve.setStatus(1);	
			reserve.setCreateTime(LocalDateTime.now());
			reserve.setVersion(checkResult.getVersion());
			reserveMapper.updateById(reserve);
			//更新上课记录的”预约检定“状态，为1
			classService.reserveClassSet(reserve);
			return 0;
		}
		
		//排课更新，预约人数增加
		scheduleMapper.updateById(schedule);
		//未预约，添加会员预约时，则设置状态为已预约
		reserve.setStatus(1);		
		reserveMapper.insert(reserve);
		//添加上课记录
		classService.saveByReserve(reserve);
		return 0;
	}
	
	//更新预约记录 - 取消预约
	@Override
	public Integer update(TReservationRecord reserve) {
		//检查状态
		Integer check = reserveCheck(reserve);
		if(check == 3) {
			System.out.println("预约取消时间过晚");
			return 3;
		}
		
		TScheduleRecord schedule = scheduleMapper.selectById(reserve.getScheduleId());
		//统计预约取消次数，当前的预约记录正要被取消
		TReservationRecord cancelled = reserveMapper.selectOne(new QueryWrapper<TReservationRecord>()
				.eq("status", 1).eq("id", reserve.getId()));
		if(cancelled != null) {
			reserve.setCancelTimes(reserve.getCancelTimes() + 1);
			System.out.println("-------预约取消次数："+reserve.getCancelTimes());
		}
		//====统一允许取消的预约次数
		if(reserve.getCancelTimes() > 3){
			System.out.println("对于这堂课可取消预约的次数已满");
			return -1;
		}

		//预约人数变动
		Integer orderNums = schedule.getOrderNums() - reserve.getReserveNums();
		if(orderNums < 0) {
			orderNums = 0;
		}
		schedule.setOrderNums(orderNums);		
		scheduleMapper.updateById(schedule);
		//预约人数满足条件时，进行更新
		reserveMapper.updateById(reserve);
		//更新上课记录的”预约检定“状态，为0
		classService.reserveClassSet(reserve);
		return 0;
	}
	
	//当前排课的已预约记录
	@Override
	public List<ReserveRecordDTO> listReserved(Long scheduleId) {
		List<ReserveRecordDTO> reserveDtoList = reserveMapper.listReservedView(scheduleId);
		return reserveDtoList;
	}
	
	//当前排课的预约记录
	@Override
	public List<ReserveRecordDTO> listReserveRecords(Long scheduleId) {
		List<ReserveRecordDTO> reserveDtoList = reserveMapper.listReserveRecordView(scheduleId);
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

	//根据会员id和排课id查询到一条预约记录
	@Override
	public TReservationRecord findOneByMS(Long memberId, Long scheduleId) {
		TReservationRecord one = reserveMapper.selectOne(new QueryWrapper<TReservationRecord>()
				.eq("member_id", memberId).eq("schedule_id", scheduleId));
		return one;
	}

}
