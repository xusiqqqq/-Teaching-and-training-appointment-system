package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.MemberBindRecordEntity;

import java.util.List;

public interface MemberBindRecordService extends IService<MemberBindRecordEntity> {

    String[] getMemberCardsNameByMemberId(Long memberId);

    Integer[] getMemberCardsIdByMemberId(Long memberId);
    List<MemberBindRecordEntity> getValidMemberBindList();
}
