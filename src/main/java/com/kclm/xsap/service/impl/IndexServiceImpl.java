package com.kclm.xsap.service.impl;

import com.kclm.xsap.dao.MemberBindRecordDao;
import com.kclm.xsap.dao.RechargeRecordDao;
import com.kclm.xsap.service.IndexService;
import com.kclm.xsap.vo.BindCardCountsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IndexServiceImpl implements IndexService {
    @Resource
    private RechargeRecordDao rechargeDao;

    @Resource
    private MemberBindRecordDao bindRecordDao;
    @Override
    public BigDecimal getChargeCountsByDay(LocalDateTime start,LocalDateTime end) {
        return rechargeDao.getChargeCountByDay(start, end);
    }

    @Override
    public List<BindCardCountsVo> getCardsBindCounts() {
        return bindRecordDao.getCardBindCounts();
    }
}
