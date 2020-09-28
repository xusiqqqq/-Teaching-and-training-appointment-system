package com.kclm.xsap.dto.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.kclm.xsap.dto.HomePageDTO;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月21日 上午10:11:28 
 * @description 此类用来描述了首页的统计数据DTO转换
 *
 */
@Mapper
public interface HomePageConvert {

	HomePageConvert INSTANCE = Mappers.getMapper(HomePageConvert.class);
	
	/**
	 * 
	 * @param totalMembers	会员总数
	 * @param reserveNums	预约总数
	 * @param activeNums	活跃用户（近一个月内预约过）
	 * @param reserveNumsList	每日约课数量
	 * @param memberNumslist	每日新增会员数量
	 * @return HomePageDTO。显示首页的统计数据
	 */
	@Mappings({
		@Mapping(source = "reserveNums",target = "totalReservations"),
		@Mapping(source = "activeNums",target = "activeMembers"),
		@Mapping(source = "reserveNumsList",target = "dailyReservations"),
		@Mapping(source = "memberNumslist",target = "dailyNewMembers"),
	})
	HomePageDTO entity2Dto(Integer totalMembers,Integer reserveNums,Integer activeNums,List<Integer> reserveNumsList,
			List<Integer> memberNumslist);
	
}
