package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.MemberEntity;
import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.vo.MemberVo;

import java.util.List;
import java.util.Map;

/**
 * 会员表
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);



    List<MemberVo> getMemberVoList();


    List<MemberEntity> getMemberLogOutInSpecifyYear(Integer endYear);

    List<MemberEntity> getMemberLogOutFromBeginYearToEndYear();
}

