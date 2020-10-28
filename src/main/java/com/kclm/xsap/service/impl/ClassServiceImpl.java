package com.kclm.xsap.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.entity.TClassRecord;
import com.kclm.xsap.entity.TConsumeRecord;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TMemberLog;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.entity.TScheduleRecord;
import com.kclm.xsap.mapper.TClassRecordMapper;
import com.kclm.xsap.mapper.TConsumeRecordMapper;
import com.kclm.xsap.mapper.TCourseMapper;
import com.kclm.xsap.mapper.TMemberBindRecordMapper;
import com.kclm.xsap.mapper.TMemberCardMapper;
import com.kclm.xsap.mapper.TMemberLogMapper;
import com.kclm.xsap.mapper.TReservationRecordMapper;
import com.kclm.xsap.mapper.TScheduleRecordMapper;
import com.kclm.xsap.service.ClassService;
import com.kclm.xsap.service.MemberCardService;

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
	
	@Autowired
	TConsumeRecordMapper consumeMapper;
	
	@Autowired
	TMemberCardMapper cardMapper;
	
	@Autowired
	TMemberBindRecordMapper bindMapper;
	
	@Autowired
	TMemberLogMapper logMapper;
	
	@Autowired
	MemberCardService cardService;
	
	//更新上课记录的”预约检定“状态
	@Override
	public boolean reserveClassSet(TReservationRecord reserve) {
			TClassRecord classRecord = classMapper.selectOne(new QueryWrapper<TClassRecord>()
					.eq("member_id",reserve.getMemberId()).eq("schedule_id", reserve.getScheduleId()));
			//变更”预约检定“
			classRecord.setReserveCheck(reserve.getStatus());
			
			//==进行卡次的运算
			TMemberCard card = cardMapper.selectOne(new QueryWrapper<TMemberCard>().eq("name", reserve.getCardName()));
			//拿到课程的消耗次数
			Integer timesCost = courseMapper.selectById((scheduleMapper.selectById(reserve.getScheduleId()).getCourseId())).getTimesCost();
			//找到会员的绑卡记录
			TMemberBindRecord bindRecord = bindMapper.selectOne(new QueryWrapper<TMemberBindRecord>()
					.eq("member_id", reserve.getMemberId()).eq("card_id", card.getId()));
			if(reserve.getStatus() == 1) {
				//表示预约
				bindRecord.setValidCount(bindRecord.getValidCount() - timesCost);
			}else {
				//取消预约 - 返还次数
				bindRecord.setValidCount(bindRecord.getValidCount() + timesCost);
			}
			
			classMapper.updateById(classRecord);
		return true;
	}
	
	//根据预约id，进行单个上课记录的录入
	@Override
	public boolean saveByReserve(TReservationRecord reserve) {
		if(reserve == null) {
			System.out.println("-------未预约或此条预约记录不存在");
			return false;
		}
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
		//已预约状态设置
		classed.setReserveCheck(reserve.getStatus());
		//录入一条上课记录
		classMapper.insert(classed);
		return true;
	}
	
	//针对“已预约”状态，进行全部录入
	@Override
	@Deprecated
	public boolean saveAll() {
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
		
		return true;
	}

	//单个上课记录更新
	@Override
	public boolean update(Long classId,Integer status) {
		TClassRecord classed = classMapper.selectById(classId);
		if(classed == null) {
			System.out.println("------没有此条上课记录");
			return false;
		}
		classed.setCheckStatus(status);
		classMapper.updateById(classed);
		return true;
	}

	//根据当前排课id，针对“未确认”上课，进行全部上课记录的更新
	@Override
	public boolean ensureByScheduleId(Long scheduleId) {
		List<TClassRecord> classList = classMapper.selectList(new QueryWrapper<TClassRecord>()
				.eq("check_status", 0).eq("reserve_check", 1).eq("schedule_id", scheduleId));
		System.out.println("---------classList------------");
		System.out.println(classList);
		System.out.println("---------------------");
		
		for (TClassRecord classed : classList) {
			classed.setCheckStatus(1);
			classMapper.update(classed,new QueryWrapper<TClassRecord>().eq("id", classed.getId()));
			
			/* ------  进行消费统计 ------ */
			TConsumeRecord consume = new TConsumeRecord();
			//会员绑定号
			consume.setMemberBindId(consume.getMemberBindId());
			//查询出当前课程单次课需花费的次数
			TScheduleRecord scheduleRecord = scheduleMapper.selectById(scheduleId);
			TCourse course = courseMapper.selectById(scheduleRecord.getCourseId());
			consume.setCardCountChange(course.getTimesCost());
			
			//为系统自动处理时，天数不进行消耗处理
			consume.setCardDayChange(0);
			
			consume.setOperateType("上课支出");
			consume.setOperator("系统自动处理");
			//消费处理
			cardService.consume(consume);
			/* ------  off ------ */
		}
		
		return true;
	}
		
	//针对“未确认”上课，进行全部上课记录的更新，暂且用不上
	@Override
	public boolean updateAll() {
		List<TClassRecord> classList = classMapper.selectList(new QueryWrapper<TClassRecord>()
				.eq("check_status", 0).eq("reserve_check", 1));
		for (TClassRecord classed : classList) {
			System.out.println("-------"+classed);
			classed.setCheckStatus(1);
			classMapper.update(classed,new QueryWrapper<TClassRecord>().eq("id", classed.getId()));
			/* ------  进行消费统计 ------ */
			TConsumeRecord consume = new TConsumeRecord();
			//会员绑定号
			consume.setMemberBindId(consume.getMemberBindId());
			//查询出当前课程单次课需花费的次数
			TScheduleRecord scheduleRecord = scheduleMapper.selectById(classed.getScheduleId());
			TCourse course = courseMapper.selectById(scheduleRecord.getCourseId());
			consume.setCardCountChange(course.getTimesCost());
			
			//为系统自动处理时，天数不进行消耗处理
			consume.setCardDayChange(0);
			
			consume.setOperateType("上课支出");
			consume.setOperator("系统自动处理");
			
			cardService.consume(consume);
			/* ------  off ------ */
			
		}
		
		return true;
	}

	@Override
	public boolean deleteOne(Long classId) {
		TClassRecord classed = classMapper.selectById(classId);
		if(classed == null) {
			System.out.println("------没有此条上课记录");
			return false;
		}
		classMapper.deleteById(classId);
		return false;
	}

	//根据id查找上课记录
	@Override
	public TClassRecord findById(Long classId) {
		TClassRecord classRecord = classMapper.selectById(classId);
		return classRecord;
	}

	//查找”已预约“记录对应的上课记录为”已确认“的一条数据 -  方法1
	@Override
	public TClassRecord findIsEnsure(Long memberId, Long scheduleId) {
		TClassRecord selectOne = classMapper.selectOne(new QueryWrapper<TClassRecord>()
				.eq("member_id", memberId).eq("schedule_id", scheduleId).eq("check_status", 1).eq("reserve_check", 1));
		return selectOne;
	}

	//查找”已预约“记录对应的上课记录为”已确认“的一条数据 - 方法2
	@Override
	public TClassRecord findIsEnsureById(Long classId) {
		TClassRecord selectOne = classMapper.selectOne(new QueryWrapper<TClassRecord>()
				.eq("id", classId).eq("check_status", 1).eq("reserve_check", 1));
		return selectOne;
	}

	
	
}
