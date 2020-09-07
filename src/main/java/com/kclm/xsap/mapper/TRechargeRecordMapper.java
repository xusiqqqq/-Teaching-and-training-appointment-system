package com.kclm.xsap.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TRechargeRecord;

public interface TRechargeRecordMapper extends BaseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TRechargeRecord record);

    int insertSelective(TRechargeRecord record);

    TRechargeRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TRechargeRecord record);

    int updateByPrimaryKey(TRechargeRecord record);
}