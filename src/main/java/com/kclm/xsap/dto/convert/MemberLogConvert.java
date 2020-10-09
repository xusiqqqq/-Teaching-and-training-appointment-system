/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.dto.convert;

import com.kclm.xsap.dto.MemberLogDTO;
import com.kclm.xsap.entity.TMemberLog;

import java.time.LocalDateTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/******************
 * @Author yejf
 * @Version v1.0
 * @Create 2020-09-25 10:26
 * @Description TODO
 */
@Mapper
public interface MemberLogConvert {

    MemberLogConvert INSTANCE = Mappers.getMapper(MemberLogConvert.class);

    /**
     * 
     * @param log	对应操作记录实体类
     * @param status	卡激活状态
     * @param cardNote	卡备注
     * @param validTimes	卡可用次数
     * @param endToDate	卡到期事件
     * @return	MemberLogDTO	要显示的操作记录
     */
    @Mappings({
    	@Mapping(source = "log.createTime",target = "operateTime"),
    	@Mapping(source = "log.type",target = "operateType")
    })
    MemberLogDTO entity2DTO(TMemberLog log,Integer status,String cardNote,Integer validTimes,LocalDateTime endToDate);
}
