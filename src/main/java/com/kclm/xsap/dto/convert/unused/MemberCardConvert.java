package com.kclm.xsap.dto.convert.unused;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.kclm.xsap.dto.MemberCardDTO;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TMemberLog;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月21日 上午9:29:32 
 * @description 此类用来描述了会员卡信息的DTO类型转换
 *
 */
@Mapper
public interface MemberCardConvert {

	MemberCardConvert INSTANCE = Mappers.getMapper(MemberCardConvert.class);
	
	/**
	 * 	未使用
	 * @return	MemberCardDTO。显示会员卡的信息
	 */
//	MemberCardDTO entity2Dto();

}

