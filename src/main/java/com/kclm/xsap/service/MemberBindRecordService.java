package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.MemberBindRecordEntity;
import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.vo.CardInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 中间表：会员绑定记录
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
public interface MemberBindRecordService extends IService<MemberBindRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);


    String[] getCardName(Long id);

    /**
     * 用于获取返回会员详情页的会员卡信息
     * @param id
     * @return
     */
    List<CardInfoVo> getCardInfo(Long id);

    int updateStatus(Long bindCardId, Integer status);
}

