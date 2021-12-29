package com.kclm.xsap.dao;

import com.kclm.xsap.entity.ClassRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.vo.ClassInfoVo;
import com.kclm.xsap.vo.ClassRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
@Mapper
public interface ClassRecordDao extends BaseMapper<ClassRecordEntity> {

    List<ClassRecordVo> selectClassRecordList(@Param("id") Long id);

    List<ClassInfoVo> selectClassInfo(@Param("id") Long id);
}
