package com.kclm.xsap.service.impl;

import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.Query;
import com.kclm.xsap.vo.ClassInfoVo;
import com.kclm.xsap.vo.ClassRecordVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.kclm.xsap.dao.ClassRecordDao;
import com.kclm.xsap.entity.ClassRecordEntity;
import com.kclm.xsap.service.ClassRecordService;


@Service("classRecordService")
public class ClassRecordServiceImpl extends ServiceImpl<ClassRecordDao, ClassRecordEntity> implements ClassRecordService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ClassRecordEntity> page = this.page(
                new Query<ClassRecordEntity>().getPage(params),
                new QueryWrapper<ClassRecordEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ClassRecordVo> getClassRecordList(Long id) {

        return baseMapper.selectClassRecordList(id);
    }

    @Override
    public List<ClassInfoVo> getClassInfo(Long id) {
        return baseMapper.selectClassInfo(id);

    }

}