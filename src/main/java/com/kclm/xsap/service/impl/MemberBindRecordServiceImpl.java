package com.kclm.xsap.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kclm.xsap.dao.MemberBindRecordDao;
import com.kclm.xsap.entity.MemberBindRecordEntity;
import com.kclm.xsap.service.MemberBindRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service("memberBindRecordService")
public class MemberBindRecordServiceImpl extends ServiceImpl<MemberBindRecordDao, MemberBindRecordEntity> implements MemberBindRecordService {

    @Resource
    private MemberBindRecordDao memberBindRecordDao;


    @Override
    public String[] getMemberCardsNameByMemberId(Long memberId) {
        return memberBindRecordDao.getMemberCardsNameByMemberId(memberId);
    }

    @Override
    public Integer[] getMemberCardsIdByMemberId(Long memberId) {
        return memberBindRecordDao.getMemberCardsIdByMemberId(memberId);
    }

    @Override
    public List<MemberBindRecordEntity> getValidMemberBindList() {
        return memberBindRecordDao.getValidBindList();
    }
}
