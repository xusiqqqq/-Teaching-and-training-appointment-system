/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.targetdto;

import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.kclm.xsap.entity.TCourse;

/******************
 * @Author yejf
 * @Version v1.0
 * @Create 2020-09-16 14:23
 * @Description TODO
 */
@Mapper
public interface CourseConvert {

	CourseConvert INSTANCE = Mappers.getMapper(CourseConvert.class);

    @Mapping(source = "name", target = "courseName")
    CourseDTO entity2Dto(TCourse course);

    @Mapping(source = "courseName", target = "name")
    TCourse dto2Entity(CourseDTO dto);

}
