package com.kclm.xsap.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.entity.TClassRecord;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.mapper.TClassRecordMapper;
import com.kclm.xsap.mapper.TReservationRecordMapper;
import com.kclm.xsap.service.ClassService;

@Service
@Transactional
public class ClassServiceImpl implements ClassService{

	@Autowired
	TReservationRecordMapper reserveMapper;
	
	@Autowired
	TClassRecordMapper classMapper;
	
	@Override
	public boolean save() {
		TClassRecord classed = new TClassRecord();
		
		//找出用户的预约记录，若其状态为“已预约”，则录入上课记录
		List<TReservationRecord> reserveList = reserveMapper.selectList(
				new QueryWrapper<TReservationRecord>().eq("status", 1));
		for (TReservationRecord reserve : reserveList) {
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
			//最后一次预约修改时间，可表示为“上课”就绪，作为上课记录创建时间
			classed.setCreateTime(reserve.getLastModifyTime());
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
