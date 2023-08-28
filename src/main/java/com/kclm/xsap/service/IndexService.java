package com.kclm.xsap.service;

import com.kclm.xsap.vo.BindCardCountsVo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface IndexService {
    //获取当月每天的收费情况
    BigDecimal getChargeCountsByDay(LocalDateTime start,LocalDateTime end);

    List<BindCardCountsVo> getCardsBindCounts();
}
