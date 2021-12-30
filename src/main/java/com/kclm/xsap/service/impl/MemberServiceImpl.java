package com.kclm.xsap.service.impl;

import com.kclm.xsap.dao.MemberBindRecordDao;
import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import com.kclm.xsap.vo.MemberVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kclm.xsap.dao.MemberDao;
import com.kclm.xsap.entity.MemberEntity;
import com.kclm.xsap.service.MemberService;


@Slf4j
@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberBindRecordDao memberBindRecordDao;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public List<MemberVo> getMemberVoList() {
        return baseMapper.selectMemberVoList();

    }

    @Override
    public List<MemberEntity> getMemberLogOutInSpecifyYear(Integer yearOfSelect) {
        return baseMapper.selectMemberLogOutSpecifyYear(yearOfSelect);
    }

    @Override
    public List<MemberEntity> getMemberLogOutFromBeginYearToEndYear() {

        return baseMapper.getMemberLogOutFromBeginYearToEndYear();
    }

    @Override
    public List<MemberEntity> getCurrentMonthLogoutMemberInfo(Integer currentYear,Integer currentMonth) {

        return baseMapper.selectCurrentMonthLogoutMemberInfo(currentYear,currentMonth);
    }


}