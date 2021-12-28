package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.GlobalReservationSetEntity;
import com.kclm.xsap.utils.PageUtils;

import java.util.Map;

/**
 * 全局预约设置表
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
public interface GlobalReservationSetService extends IService<GlobalReservationSetEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

