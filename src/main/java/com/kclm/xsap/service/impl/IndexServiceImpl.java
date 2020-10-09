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
import com.kclm.xsap.dto.HomePageDTO;
import com.kclm.xsap.dto.ReportDTO;
import com.kclm.xsap.dto.convert.HomePageConvert;
import com.kclm.xsap.dto.convert.ReportConvert;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.mapper.TMemberBindRecordMapper;
import com.kclm.xsap.mapper.TMemberCardMapper;
import com.kclm.xsap.mapper.TMemberMapper;
import com.kclm.xsap.mapper.TReservationRecordMapper;
import com.kclm.xsap.service.IndexService;

@Service
@Transactional
public class IndexServiceImpl implements IndexService{

	@Autowired
	TMemberMapper memberMapper;
	
	@Autowired
	TReservationRecordMapper reserveMapper;
	
	@Override
	public HomePageDTO queryByDate(LocalDate startDate, LocalDate endDate) {
		//==会员总数
		List<TMember> memberList = memberMapper.selectList(null);
		int totalMembers = memberList.size();

		//==预约总数
		List<TReservationRecord> reserveList = reserveMapper.selectList(null);
		int reserveNums = reserveList.size();
		
		//==活跃用户(近一个月内预约过的)
		LocalDateTime nowTime = LocalDateTime.now();
		LocalDateTime inOneMonth = nowTime.minusMonths(1);
		reserveList = reserveMapper.selectList(new QueryWrapper<TReservationRecord>()
				.between("create_time",inOneMonth , nowTime));
		List<Long> idList = new ArrayList<>();
		for(int i = 0 ;i < reserveList.size(); i++) {
			 Long memberId = reserveList.get(i).getMemberId();
			 idList.add(memberId);
		}
		int activeNums = idList.size();
		
		//==每日约课数量
		List<Integer> reserveNumsList = new ArrayList<>();
		//对日期区间做LocalDateTime类型转换
		LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
		LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
		//用来日期自增，从指定的开始日期开始
		LocalDateTime changeDateTime = startDateTime;
		
		//计算输入日期的间隔天数endDate - startDate
		Long getDays = endDate.toEpochDay() - startDate.toEpochDay();
		
		for(int i = 0; i< getDays; i++) {
			reserveList = reserveMapper.selectList(new QueryWrapper<TReservationRecord>()
					.eq("status", 1).between("create_time", changeDateTime, endDateTime));
			//存入当天的预约数量
			reserveNumsList.add(reserveList.size());
			//自增一天
			changeDateTime = changeDateTime.plusDays(1); 
		}
		
		//==每日新增会员数量
		changeDateTime = startDateTime;
		List<Integer> memberNumslist = new ArrayList<>();
		for(int i = 0; i< getDays; i++) {
			List<TMember> memberNums = memberMapper.selectList(new QueryWrapper<TMember>()
					.between("create_time", changeDateTime, endDateTime));
			//存入当天新增会员的数量
			memberNumslist.add(memberNums.size());
			//自增一天
			changeDateTime = changeDateTime.plusDays(1);
		}
		//==DTO组合
		HomePageDTO homePageDto = HomePageConvert.INSTANCE.entity2Dto(totalMembers, reserveNums, activeNums, reserveNumsList, memberNumslist);
		
		return homePageDto;
	}

	@Autowired
	TMemberCardMapper cardMapper;
	
	@Autowired
	TMemberBindRecordMapper bindMapper;
	
	@Override
	public List<ReportDTO> statistic() {
		List<ReportDTO> reportDtoList = new ArrayList<>();
		ReportDTO reportDto;
		Map<String,Integer> data = new HashMap<String, Integer>();
		//会员卡种类名称+会员卡绑定数量
		List<TMemberCard> cardList = cardMapper.selectList(null);
		for (TMemberCard card : cardList) {
			List<TMemberBindRecord> bindList = bindMapper.selectList(new QueryWrapper<TMemberBindRecord>().eq("card_id", card.getId()));
			data.put(card.getName(), bindList.size());
			//DTO转换
			reportDto = ReportConvert.INSTANCE.entity2Dto(data);
			reportDtoList.add(reportDto);
		}
		return reportDtoList;
	}

}
