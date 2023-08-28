package com.kclm.xsap.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
@SpringBootTest
public class MemberBindRecordDaoTest {
    @Resource
    private MemberBindRecordDao dao;

    @Test
    public void test() {
        String[] cardnames = dao.getMemberCardsNameByMemberId(85L);
        for (String cardname : cardnames) {
            System.out.println(cardname);
        }
    }
}
