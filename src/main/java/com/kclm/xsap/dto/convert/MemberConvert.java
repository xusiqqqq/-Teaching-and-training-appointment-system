package com.kclm.xsap.dto.convert;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.ConsumeRecordDTO;
import com.kclm.xsap.dto.MemberCardDTO;
import com.kclm.xsap.dto.MemberDTO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TMember;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月21日 上午9:41:32 
 * @description 此类用来描述了会员的DTO类型转换
 *
 */
@Mapper
public interface MemberConvert {

	MemberConvert INSTANCE = Mappers.getMapper(MemberConvert.class);
	
	/**
	 * 
	 * @param member	对应会员实体类
	 * @param card	对应会员卡DTO数据
	 * @param classData	对应上课记录DTO数据
	 * @param reverse	对应预约记录DTO数据
	 * @param consume	对应消费记录DTO数据
	 * @return	MemberDTO。显示的会员信息
	 */
	@Mapping(source = "member.sex",target = "gender")
	MemberDTO entity2Dto(TMember member,MemberCardDTO card,ClassRecordDTO classData,ReserveRecordDTO reverse,ConsumeRecordDTO consume);
}
