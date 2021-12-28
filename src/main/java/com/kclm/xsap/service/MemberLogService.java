package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.MemberLogEntity;
import com.kclm.xsap.utils.PageUtils;

import java.util.Map;

/**
 * 操作记录
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
public interface MemberLogService extends IService<MemberLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

