package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.ClassRecordEntity;
import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.vo.ClassInfoVo;
import com.kclm.xsap.vo.ClassRecordVo;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
public interface ClassRecordService extends IService<ClassRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ClassRecordVo> getClassRecordList(Long id);

    List<ClassInfoVo> getClassInfo(Long id);
}

