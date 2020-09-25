/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.dto.convert;

import com.kclm.xsap.dto.MemberLogDTO;
import com.kclm.xsap.entity.TMemberLog;
import org.mapstruct.Mapper;
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

    MemberLogDTO entity2DTO(TMemberLog entity);
}
