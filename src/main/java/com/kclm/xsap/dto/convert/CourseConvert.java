package com.kclm.xsap.dto.convert;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.kclm.xsap.dto.CourseDTO;
import com.kclm.xsap.entity.TCourse;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月18日 下午5:47:05 
 * @description 此类用来描述了课程DTO类型转换
 *
 */
@Mapper
public interface CourseConvert {

	CourseConvert INSTANCE = Mappers.getMapper(CourseConvert.class);
	
	/**
	 * 
	 * @param course	对应课程实体类
	 * @return	CourseDTO。课程的展示信息
	 */
	CourseDTO entity2Dto(TCourse course);
	
}
