package com.kclm.xsap.dao;

import com.kclm.xsap.entity.MemberCardEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员卡表
 * 
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@Mapper
public interface MemberCardDao extends BaseMapper<MemberCardEntity> {

    List<MemberCardEntity> selectCardList(List<Long> cardIds);
}
