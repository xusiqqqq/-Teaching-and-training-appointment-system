package com.kclm.xsap.service.impl;

import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import com.kclm.xsap.vo.ConsumeInfoVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kclm.xsap.dao.ConsumeRecordDao;
import com.kclm.xsap.entity.ConsumeRecordEntity;
import com.kclm.xsap.service.ConsumeRecordService;


@Service("consumeRecordService")
public class ConsumeRecordServiceImpl extends ServiceImpl<ConsumeRecordDao, ConsumeRecordEntity> implements ConsumeRecordService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ConsumeRecordEntity> page = this.page(
                new Query<ConsumeRecordEntity>().getPage(params),
                new QueryWrapper<ConsumeRecordEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ConsumeInfoVo> getConsumeInfo(Long id) {

        return baseMapper.selectConsumeInfo(id);
    }

}