package com.kclm.xsap.dao;

import com.kclm.xsap.service.ConsumeRecordService;
import com.kclm.xsap.vo.TeacherClassCostVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class ConsumeRecordDaoTest {
    @Resource
    private ConsumeRecordDao dao;
    @Resource
    private ConsumeRecordService service;

    @Test
    public void test(){
        LocalDateTime start=LocalDateTime.of(2021,12,30,0,0,0);
        LocalDateTime end=LocalDateTime.of(2022,12,30,0,0,0);
        List<TeacherClassCostVo> teacherClassConsume = service.getTeacherClassCostVo(start, end);
        for (TeacherClassCostVo teacherClassCostVo : teacherClassConsume) {
            System.out.println(teacherClassCostVo);
        }
    }
}
