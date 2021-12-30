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


    /**
     * 查询指定年份的所有注销用户，按注销时间倒序
     * @param endYear 指定年份
     * @return 所有注销用户
     */
    List<MemberEntity> getMemberLogOutInSpecifyYear(Integer yearOfSelect);

    /**
     *
     * @return 所有注销用户；按注销时间倒序
     */
    List<MemberEntity> getMemberLogOutFromBeginYearToEndYear();

    /**
     *
     * @param currentMonth 当前月份的字符串 "yyyy-MM"
     * @return 返回所有当前月份的注销用户信息
     */
    List<MemberEntity> getCurrentMonthLogoutMemberInfo(Integer currentYear, Integer currentMonth);
}

