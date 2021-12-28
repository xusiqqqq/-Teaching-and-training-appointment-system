package com.kclm.xsap.service.impl;

import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kclm.xsap.dao.MemberCardDao;
import com.kclm.xsap.entity.MemberCardEntity;
import com.kclm.xsap.service.MemberCardService;


@Service("memberCardService")
public class MemberCardServiceImpl extends ServiceImpl<MemberCardDao, MemberCardEntity> implements MemberCardService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberCardEntity> page = this.page(
                new Query<MemberCardEntity>().getPage(params),
                new QueryWrapper<MemberCardEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<MemberCardEntity> getCardList(List<Long> cardIds) {
        return baseMapper.selectCardList(cardIds);

    }

}