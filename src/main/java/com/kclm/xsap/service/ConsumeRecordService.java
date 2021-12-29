package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.ConsumeRecordEntity;
import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.vo.ConsumeInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 消费记录
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
public interface ConsumeRecordService extends IService<ConsumeRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ConsumeInfoVo> getConsumeInfo(Long id);
}

