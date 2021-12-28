package com.kclm.xsap.service.impl;

import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kclm.xsap.dao.GlobalReservationSetDao;
import com.kclm.xsap.entity.GlobalReservationSetEntity;
import com.kclm.xsap.service.GlobalReservationSetService;


@Service("globalReservationSetService")
public class GlobalReservationSetServiceImpl extends ServiceImpl<GlobalReservationSetDao, GlobalReservationSetEntity> implements GlobalReservationSetService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<GlobalReservationSetEntity> page = this.page(
                new Query<GlobalReservationSetEntity>().getPage(params),
                new QueryWrapper<GlobalReservationSetEntity>()
        );

        return new PageUtils(page);
    }

}