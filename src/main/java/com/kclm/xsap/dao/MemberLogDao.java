package com.kclm.xsap.dao;

import com.kclm.xsap.entity.MemberLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作记录
 * 
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@Mapper
public interface MemberLogDao extends BaseMapper<MemberLogEntity> {
	
}
