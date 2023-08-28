package com.kclm.xsap.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class EmployeeDaoTest {
    @Resource
    private EmployeeDao dao;

    @Test
    public void test(){
        System.out.println(System.getProperty("user.dir"));
    }
}
