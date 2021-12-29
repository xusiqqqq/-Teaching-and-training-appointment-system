package com.kclm.xsap.dao;

import com.kclm.xsap.entity.RechargeRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.vo.OperateRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 充值记录
 * 
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@Mapper
public interface RechargeRecordDao extends BaseMapper<RechargeRecordEntity> {

    List<OperateRecordVo> selectOperateRecord(@Param("memberId") Long memberId, @Param("bindId") Long bindId);
}
