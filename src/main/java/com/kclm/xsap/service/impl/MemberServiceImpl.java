package com.kclm.xsap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kclm.xsap.dao.MemberBindRecordDao;
import com.kclm.xsap.dao.MemberDao;
import com.kclm.xsap.entity.MemberEntity;
import com.kclm.xsap.service.MemberService;
import com.kclm.xsap.vo.MemberVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Resource
    private MemberDao memberDao;
    @Resource
    private MemberBindRecordDao memberBindRecordDao;

    /**
     * 将会员的id、姓名、性别、创建事件、备注、以及所拥有的会员卡  等信息封装成MemberVo对象
     * @return List<MemberVo>
     */
    @Override
    public List<MemberVo> getAllMemberVo() {
        QueryWrapper<MemberEntity> qw=new QueryWrapper<>();
        qw.select("id","name","phone","sex","create_time","note");
        List<MemberEntity> members = memberDao.selectList(qw);
        List<MemberVo> memberVos=new ArrayList<>();
        for (MemberEntity member : members) {
            String[] cards = memberBindRecordDao.getMemberCardsNameByMemberId(member.getId());
            MemberVo memberVo=new MemberVo();
            memberVo.setId(member.getId());
            memberVo.setMemberName(member.getName() + '(' + member.getPhone() + ')');
            memberVo.setPhone(member.getPhone());
            memberVo.setGender(member.getSex());
            if(member.getCreateTime()!=null) memberVo.setJoiningDate(member.getCreateTime().toLocalDate());
            memberVo.setNote(member.getNote());
            memberVo.setCardHold(cards);
            memberVos.add(memberVo);
        }
        return memberVos;
    }

    @Override
    public Integer getDeletedMemberCountsBetween(LocalDateTime start, LocalDateTime end) {
        return memberDao.getDeletedCountsBetween(start, end);
    }

    @Override
    public List<MemberEntity> getLostMemberCurrentMonth(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start=LocalDateTime.of(now.getYear(),now.getMonth(),1,0,0,0);
        return memberDao.getDeletedMemberCurrentMonth(start);

    }

    @Override
    public String getMemberNameByMemberId(Long memberId) {
        memberDao.selectById(memberId);
        return null;
    }

    @Override
    public Integer getActiveMemberCounts(LocalDateTime currentTime) {
        Integer counts = memberDao.getActiveMembers(currentTime.plusMonths(-1));
        return counts==null?0:counts;
    }

}
