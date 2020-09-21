package com.kclm.xsap.dto.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.kclm.xsap.dto.ReportDTO;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.entity.TMemberCard;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月21日 上午9:49:41 
 * @description 此类用来描述了首页图表数据DTO转换
 *
 */
@Mapper
public interface ReportConvert {

	ReportConvert INSTANCE = Mappers.getMapper(ReportConvert.class);
	
	/**
	 * 
	 * @param card	对应会员卡实体类
	 * @param bindRecord	对应会员卡绑定记录
	 * @return	ReportDTO。显示的报表信息
	 */
	ReportDTO entity2Dto(TMemberCard card,TMemberBindRecord bindRecord);
	
}
