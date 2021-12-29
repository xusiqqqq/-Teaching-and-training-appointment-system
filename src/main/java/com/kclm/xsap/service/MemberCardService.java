package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.MemberCardEntity;
import com.kclm.xsap.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 会员卡表
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
public interface MemberCardService extends IService<MemberCardEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<MemberCardEntity> getCardList(List<Long> cardIds);
}

