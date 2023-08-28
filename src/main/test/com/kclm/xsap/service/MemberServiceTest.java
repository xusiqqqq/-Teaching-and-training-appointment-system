package com.kclm.xsap.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kclm.xsap.dao.MemberBindRecordDao;
import com.kclm.xsap.entity.MemberBindRecordEntity;
import com.kclm.xsap.vo.MemberVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class MemberServiceTest {
    @Resource
    private MemberBindRecordService bindRecordService;
    @Resource
    private MemberService ser;

    @Resource
    private MemberBindRecordService bindService;
    @Resource
    private MemberBindRecordDao bindDao;

    @Test
    public void test() {
        List<MemberBindRecordEntity> validBindList = bindDao.getValidBindList();

        List<MemberBindRecordEntity> validMemberBindList = bindService.getValidMemberBindList();
        System.out.println(validBindList);
        for (MemberBindRecordEntity bindRecord : validMemberBindList) {
            System.out.println(bindRecord);
        }
//        LambdaQueryWrapper<MemberBindRecordEntity> qw=new LambdaQueryWrapper<>();
//        qw.eq(MemberBindRecordEntity::getMemberId,85L);
//        List<MemberBindRecordEntity> list = bindRecordService.list(qw);
//        for (MemberBindRecordEntity memberBindRecordEntity : list) {
//            System.out.println(memberBindRecordEntity);
//        }


//        List<MemberVo> allMemberVo = ser.getAllMemberVo();
//        for (MemberVo memberVo : allMemberVo) {
//            System.out.println(memberVo);
//        }
    }
}
