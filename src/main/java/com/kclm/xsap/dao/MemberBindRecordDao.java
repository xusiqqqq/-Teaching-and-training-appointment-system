package com.kclm.xsap.dao;

import com.kclm.xsap.entity.MemberBindRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.vo.CardInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 中间表：会员绑定记录
 * 
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@Mapper
public interface MemberBindRecordDao extends BaseMapper<MemberBindRecordEntity> {

    List<CardInfoVo> selectCardInfo(@Param("id") Long id);

    int updateStatus(@Param("bindCardId") Long bindCardId, @Param("status") Integer status);
}
