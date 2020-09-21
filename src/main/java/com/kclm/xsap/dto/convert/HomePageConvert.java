package com.kclm.xsap.dto.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.kclm.xsap.dto.HomePageDTO;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TReservationRecord;

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
	 * @param member	对应会员实体类
	 * @param reserve	对应预约记录
	 * @return	HomePageDTO。显示首页的统计数据
	 */
	HomePageDTO entity2Dto(TMember member,TReservationRecord reserve);
	
}
