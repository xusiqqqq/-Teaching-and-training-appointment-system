package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.RechargeRecordEntity;
import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.vo.OperateRecordVo;

import java.util.List;
import java.util.Map;

/**
 * 充值记录
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
public interface RechargeRecordService extends IService<RechargeRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);


    List<OperateRecordVo> getOperateRecord(Long memberId, Long bindId);
}

