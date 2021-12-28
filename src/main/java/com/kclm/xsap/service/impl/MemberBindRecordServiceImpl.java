package com.kclm.xsap.service.impl;

import com.kclm.xsap.entity.MemberCardEntity;
import com.kclm.xsap.service.MemberCardService;
import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import com.kclm.xsap.vo.CardInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kclm.xsap.dao.MemberBindRecordDao;
import com.kclm.xsap.entity.MemberBindRecordEntity;
import com.kclm.xsap.service.MemberBindRecordService;

import javax.swing.event.AncestorEvent;

@Slf4j
@Service("memberBindRecordService")
public class MemberBindRecordServiceImpl extends ServiceImpl<MemberBindRecordDao, MemberBindRecordEntity> implements MemberBindRecordService {

    @Autowired
    private MemberCardService memberCardService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberBindRecordEntity> page = this.page(
                new Query<MemberBindRecordEntity>().getPage(params),
                new QueryWrapper<MemberBindRecordEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public String[] getCardName(Long id) {
        //先查出关联表中cardId
        List<MemberBindRecordEntity> memberBindRecordEntityWithCardIdList = baseMapper
                .selectList(new QueryWrapper<MemberBindRecordEntity>()
                        .select("card_id")
                        .eq("member_id", id));

        List<Long> cardIds = memberBindRecordEntityWithCardIdList.stream().map(MemberBindRecordEntity::getCardId).collect(Collectors.toList());
        log.debug("cardIds:{}",cardIds);

        //再通过id在card表中查出卡名字
        String[] names = null;
        if (cardIds.size() != 0) {
            List<MemberCardEntity> memberCardEntityWithNameList = memberCardService.listByIds(cardIds);

            names = memberCardEntityWithNameList.stream().map(MemberCardEntity::getName).toArray(String[]::new);
        }



        log.debug("查出的name[]：{}", names);

        return names;
    }

    @Override
    public List<CardInfoVo> getCardInfo(Long id) {

        return baseMapper.selectCardInfo(id);
    }

    @Override
    public int updateStatus(Long bindCardId, Integer status) {

        return baseMapper.updateStatus(bindCardId, status);
    }


}