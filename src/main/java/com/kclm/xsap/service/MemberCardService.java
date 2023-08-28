package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.MemberCardEntity;
import com.kclm.xsap.vo.LogInfoVo;

import java.util.List;

public interface MemberCardService extends IService<MemberCardEntity> {

    public List<LogInfoVo> getLogInfoVos(Long bindId);
    public boolean saveCardCourse(MemberCardEntity card, Long[] courseListStr);
}
