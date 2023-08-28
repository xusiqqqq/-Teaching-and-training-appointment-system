package com.kclm.xsap.dao;

import com.kclm.xsap.vo.ScheduleAddValidVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class ScheduleRecordDaoTest {
    @Resource
    private ScheduleRecordDao dao;

    @Test
    public void test(){
        Long teacherId= 2L;
        LocalDateTime start=LocalDateTime.of(2022,1,1,0,0,0);
        LocalDateTime end=LocalDateTime.of(2023,8,22,0,0,0);
        List<ScheduleAddValidVo> list = dao.getScheduleAddValidVo(teacherId, start, end);
        for (ScheduleAddValidVo vo : list) {
            System.out.println(vo);
        }
    }
}
