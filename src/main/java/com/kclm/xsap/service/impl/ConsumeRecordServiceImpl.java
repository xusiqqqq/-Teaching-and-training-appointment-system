package com.kclm.xsap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kclm.xsap.dao.ConsumeRecordDao;
import com.kclm.xsap.entity.ConsumeRecordEntity;
import com.kclm.xsap.service.ConsumeRecordService;
import com.kclm.xsap.vo.TeacherClassCostVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service("consumeRecordService")
public class ConsumeRecordServiceImpl extends ServiceImpl<ConsumeRecordDao,ConsumeRecordEntity> implements ConsumeRecordService {
    @Resource
    private ConsumeRecordDao dao;
    @Override
    public BigDecimal getAllCostByBindId(Long bindId) {
        BigDecimal countsMoney = dao.getCountsMoneyByBindId(bindId);
        return countsMoney==null?new BigDecimal(0):countsMoney;
    }

    @Override
    public Integer getCostTimesByBindId(Long bindId) {
        Integer costTimes = dao.getTimesByBindId(bindId);
        return costTimes==null?0:costTimes;
    }

    @Override
    public List<TeacherClassCostVo> getTeacherClassCostVo(LocalDateTime start, LocalDateTime end) {
        return dao.getTeacherClassConsume(start,end);
    }

    @Override
    public Integer getClassTimeCounts(LocalDateTime start, LocalDateTime end) {
        Integer counts = dao.getClassTimes(start, end);
        return counts==null?0:counts;
    }
}
