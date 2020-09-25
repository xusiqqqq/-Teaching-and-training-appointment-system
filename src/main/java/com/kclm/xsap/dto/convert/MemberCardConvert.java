package com.kclm.xsap.dto.convert;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
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
	 * 
	 * @param validTimes	会员卡可用次数
	 * @param endTime	会员卡到期时间
	 * @param memberCard	会员卡实体类
	 * @return	MemberCardDTO。显示会员卡的信息
	 */
	@Mappings({
		@Mapping(source = "validTimes",target = "totalCount"),
		@Mapping(source = "endTime",target = "dueTime"),
		@Mapping(source = "memberCard.id", target = "memberCardId")
	})
	MemberCardDTO entity2Dto(Integer validTimes,LocalDateTime endTime,TMemberCard memberCard);

}

