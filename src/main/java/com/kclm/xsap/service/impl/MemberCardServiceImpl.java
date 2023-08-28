package com.kclm.xsap.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kclm.xsap.dao.MemberCardDao;
import com.kclm.xsap.entity.CourseCardEntity;
import com.kclm.xsap.entity.MemberCardEntity;
import com.kclm.xsap.entity.MemberLogEntity;
import com.kclm.xsap.service.CourseCardService;
import com.kclm.xsap.service.MemberCardService;
import com.kclm.xsap.service.MemberLogService;
import com.kclm.xsap.vo.LogInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service("memberCardService")
public class MemberCardServiceImpl extends ServiceImpl<MemberCardDao, MemberCardEntity> implements MemberCardService {
    @Autowired
    private MemberLogService memberLogService;
    @Autowired
    private CourseCardService courseCardService;
    public List<LogInfoVo> getLogInfoVos(Long bindId) {
        LambdaQueryWrapper<MemberLogEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(MemberLogEntity::getMemberBindId, bindId);
        List<MemberLogEntity> logList = memberLogService.list(qw);
        List<LogInfoVo> list=new ArrayList<>();
        for (MemberLogEntity log : logList) {
            LogInfoVo logVo=new LogInfoVo();
            logVo.setId(log.getId());
            logVo.setOperateTime(log.getCreateTime());
            logVo.setCardNote(log.getNote());
            logVo.setOperateType(log.getType());
            logVo.setChangeCount(log.getCardCountChange());
            if(logVo.getOperateType().contains("费")) logVo.setChangeMoney(-log.getInvolveMoney().intValue());
            else logVo.setChangeMoney(log.getInvolveMoney().intValue());
            logVo.setStatus(log.getCardActiveStatus());
            logVo.setOperator(log.getOperator());
            list.add(logVo);
        }
        return list;
    }

    //用来存储一张卡能上的多个课程
    public boolean saveCardCourse(MemberCardEntity card, Long[] courseListStr) {
        CourseCardEntity courseCard=new CourseCardEntity();
        courseCard.setCardId(card.getId());
        boolean b=true;
        if(courseListStr!=null&&courseListStr.length!=0){
            for (Long id : courseListStr) {
                courseCard.setCourseId(id);
                b = courseCardService.save(courseCard);
                if(!b) break;
            }
        }
        return b;
    }


}
