package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.MemberEntity;
import com.kclm.xsap.vo.MemberVo;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberService extends IService<MemberEntity> {
    //获取所有的会员Vo对象
    List<MemberVo> getAllMemberVo();

    Integer getDeletedMemberCountsBetween(LocalDateTime start,LocalDateTime end);

    List<MemberEntity> getLostMemberCurrentMonth();


    String getMemberNameByMemberId(Long memberId);

    Integer getActiveMemberCounts(LocalDateTime currentTime);
}
