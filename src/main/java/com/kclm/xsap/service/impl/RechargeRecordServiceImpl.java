package com.kclm.xsap.service.impl;

import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import com.kclm.xsap.vo.OperateRecordVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kclm.xsap.dao.RechargeRecordDao;
import com.kclm.xsap.entity.RechargeRecordEntity;
import com.kclm.xsap.service.RechargeRecordService;


@Service("rechargeRecordService")
public class RechargeRecordServiceImpl extends ServiceImpl<RechargeRecordDao, RechargeRecordEntity> implements RechargeRecordService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<RechargeRecordEntity> page = this.page(
                new Query<RechargeRecordEntity>().getPage(params),
                new QueryWrapper<RechargeRecordEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<OperateRecordVo> getOperateRecord(Long memberId, Long bindId) {

        return baseMapper.selectOperateRecord(memberId, bindId);
    }

}