package com.kclm.xsap.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@SpringBootTest
public class RechargeRecordDaoTest {
    @Resource
    private RechargeRecordDao dao;


    @Test
    public void test(){
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start=LocalDateTime.of(2021,1,1,0,0);
        System.out.println("从2021-1-1到现在的总收入："+dao.getChargeCountByDay(start,end));

    }

}
