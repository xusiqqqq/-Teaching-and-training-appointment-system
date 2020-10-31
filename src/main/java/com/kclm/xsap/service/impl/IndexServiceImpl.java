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

@Service
@Transactional
public class IndexServiceImpl implements IndexService{

//	@Autowired
//	private ReportConvert reportConvert;
//
//	@Autowired
//	private HomePageConvert homePageConvert;

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
		int activeNums = reserveList.size();

		if(startDate == null || endDate == null) {
			//=========DTO存储
			HomePageDTO homePageDto = new HomePageDTO();
			homePageDto.setStartDate(startDate);
			homePageDto.setEndDate(endDate);
			homePageDto.setTotalMembers(totalMembers);
			homePageDto.setActiveMembers(activeNums);
			homePageDto.setTotalReservations(reserveNums);
			return homePageDto;
		}
		
		//==每日约课数量
		List<Integer> reserveNumsList = new ArrayList<>();
		//对日期区间做LocalDateTime类型转换
		LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
		//用来日期自增，从指定的开始日期开始
		LocalDateTime changeDateTime = startDateTime;
		
		//计算输入日期的间隔天数endDate - startDate
		Long getDays = endDate.toEpochDay() - startDate.toEpochDay();
		
		//日期区间
		List<LocalDate> dateList = new ArrayList<LocalDate>();
		
		for(int i = 0; i<= getDays; i++) {
			//逐个保存日期
			dateList.add(startDate.plusDays(i));
			//当天的预约情况
			reserveList = reserveMapper.selectList(new QueryWrapper<TReservationRecord>()
					.eq("status", 1).between("create_time", changeDateTime, 
					LocalDateTime.of(changeDateTime.toLocalDate(), LocalTime.MAX)));
			//存入当天的预约数量
			reserveNumsList.add(reserveList.size());
			//自增一天
			changeDateTime = changeDateTime.plusDays(1); 
		}
		
		//==每日新增会员数量
		changeDateTime = startDateTime;
		List<Integer> memberNumslist = new ArrayList<>();
		for(int i = 0; i<= getDays; i++) {
			//当天的会员新增情况
			List<TMember> memberNums = memberMapper.selectList(new QueryWrapper<TMember>()
					.between("create_time", changeDateTime, 
							LocalDateTime.of(changeDateTime.toLocalDate(), LocalTime.MAX)));
			//存入当天新增会员的数量
			memberNumslist.add(memberNums.size());
			//自增一天
			changeDateTime = changeDateTime.plusDays(1);
		}
		//=========DTO存储
		HomePageDTO homePageDto = new HomePageDTO();
		homePageDto.setStartDate(startDate);
		homePageDto.setEndDate(endDate);
		homePageDto.setTotalMembers(totalMembers);
		homePageDto.setActiveMembers(activeNums);
		homePageDto.setTotalReservations(reserveNums);
		homePageDto.setDailyReservations(reserveNumsList);
		homePageDto.setDailyNewMembers(memberNumslist);
		homePageDto.setDateList(dateList);
		return homePageDto;
	}

	@Autowired
	TMemberCardMapper cardMapper;
	
	@Autowired
	TMemberBindRecordMapper bindMapper;
	
	@Override
	public List<ReportDTO> statistic() {
		List<ReportDTO> reportDtoList = new ArrayList<>();
		//会员卡种类名称+会员卡绑定数量
		List<TMemberCard> cardList = cardMapper.selectList(null);
		for (int i = 0 ; i < cardList.size() ; i++) {
			TMemberCard card = 	cardList.get(i);
			List<TMemberBindRecord> bindList = bindMapper.selectList(new QueryWrapper<TMemberBindRecord>()
					.eq("card_id", card.getId()));
			//=======DTO存储
			ReportDTO reportDto = new ReportDTO();
			reportDto.setName(card.getName());
			reportDto.setValue(bindList.size());
			reportDtoList.add(reportDto);
		}
		return reportDtoList;
	}

}
