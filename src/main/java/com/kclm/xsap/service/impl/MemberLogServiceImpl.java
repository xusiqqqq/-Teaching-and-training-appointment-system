package com.kclm.xsap.service.impl;

import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kclm.xsap.dao.MemberLogDao;
import com.kclm.xsap.entity.MemberLogEntity;
import com.kclm.xsap.service.MemberLogService;


@Service("memberLogService")
public class MemberLogServiceImpl extends ServiceImpl<MemberLogDao, MemberLogEntity> implements MemberLogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLogEntity> page = this.page(
                new Query<MemberLogEntity>().getPage(params),
                new QueryWrapper<MemberLogEntity>()
        );

        return new PageUtils(page);
    }

}