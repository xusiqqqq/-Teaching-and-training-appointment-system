package com.kclm.xsap.dto.convert;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.kclm.xsap.dto.ReportDTO;

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
	 * @param data	图表数据
	 * @return	ReportDTO。显示的报表信息
	 */

	@Mapping(source = "data", target = "memberCardBindingMap")
	ReportDTO entity2Dto(Map<String,Integer> data);
	
}

