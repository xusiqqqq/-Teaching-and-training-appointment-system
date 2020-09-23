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
import com.kclm.xsap.dto.HomePageDTO;
import com.kclm.xsap.dto.ReportDTO;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TReservationRecord;
import com.kclm.xsap.mapper.TMemberBindRecordMapper;
import com.kclm.xsap.mapper.TMemberCardMapper;
import com.kclm.xsap.mapper.TMemberMapper;
import com.kclm.xsap.mapper.TReservationRecordMapper;
import com.kclm.xsap.service.IndexService;

public class IndexServiceImpl implements IndexService{

	@Autowired
	TMemberMapper memberMapper;
	
	@Autowired
	TReservationRecordMapper reserveMapper;
	
	@Override
	public HomePageDTO queryByDate(LocalDate startDate, LocalDate endDate) {
		HomePageDTO homePageDto = new HomePageDTO();

		//会员总数
		List<TMember> memberList = memberMapper.selectList(null);
		int totalMembers = memberList.size();

		homePageDto.setTotalMembers(totalMembers);
		//预约总数
		List<TReservationRecord> reserveList = reserveMapper.selectList(null);
		int reserveNums = reserveList.size();
		
		homePageDto.setTotalReservations(reserveNums);
		//活跃用户(近一个月内预约过的)
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
		
		homePageDto.setActiveMembers(activeNums);
		
		//每日约课数量
		List<Integer> numberList = new ArrayList<>();
		//对日期区间做LocalDateTime类型转换
		LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
		LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
		//用来日期自增，从指定的开始日期开始
		LocalDateTime changeDateTime = startDateTime;
		
		/* 待处理  - begin*/
		//计算输入日期的间隔天数endDate - startDate
		int getDays = 1;
		/* 待处理  - end*/
		
		for(int i = 0; i< getDays; i++) {
			reserveList = reserveMapper.selectList(new QueryWrapper<TReservationRecord>()
					.eq("status", 1).between("create_time", changeDateTime, endDateTime));
			//存入当天的预约数量
			numberList.add(reserveList.size());
			//自增一天
			changeDateTime = changeDateTime.plusDays(1); 
		}
		
		homePageDto.setDailyReservations(numberList);
		
		//每日新增会员数量
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
		
		homePageDto.setDailyNewMembers(memberNumslist);
		
		return homePageDto;
	}

	@Autowired
	TMemberCardMapper cardMapper;
	
	@Autowired
	TMemberBindRecordMapper bindMapper;
	
	@Override
	public List<ReportDTO> statistic() {
		List<ReportDTO> reportDtoList = new ArrayList<>();
		ReportDTO reportDto = new ReportDTO();
		Map<String,Integer> data = new HashMap<String, Integer>();
		//会员卡种类名称+会员卡绑定数量
		List<TMemberCard> cardList = cardMapper.selectList(null);
		for (TMemberCard card : cardList) {
			List<TMemberBindRecord> bindList = bindMapper.selectList(new QueryWrapper<TMemberBindRecord>().eq("card_id", card.getId()));
			data.put(card.getName(), bindList.size());
			reportDto.setMemberCardBindingMap(data);
			reportDtoList.add(reportDto);
		}
		return reportDtoList;
	}

}
