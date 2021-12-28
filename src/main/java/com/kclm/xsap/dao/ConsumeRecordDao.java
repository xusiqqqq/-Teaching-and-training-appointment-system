package com.kclm.xsap.dao;

import com.kclm.xsap.entity.ConsumeRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.vo.ConsumeInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消费记录
 * 
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
@Mapper
public interface ConsumeRecordDao extends BaseMapper<ConsumeRecordEntity> {

    List<ConsumeInfoVo> selectConsumeInfo(@Param("id") Long id);
}
