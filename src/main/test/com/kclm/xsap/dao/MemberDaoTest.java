package com.kclm.xsap.dao;

import com.kclm.xsap.entity.MemberEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class MemberDaoTest {
    @Resource
    private MemberDao dao;


    @Test
    public void test(){
//        LocalDateTime end = LocalDateTime.now();
//        LocalDateTime start=LocalDateTime.of(2021,1,1,0,0);
//        List<MemberEntity> hasDeleteMember = dao.getDeletedMemberCountsBetween(start, end);
//        for (MemberEntity member : hasDeleteMember) {
//            System.out.println(member);
//        }

    }
}
