package com.kclm.xsap.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TCourse;

@Mapper
public interface TCourseMapper extends BaseMapper<TCourse> {
	
	List<TCourse> findAll();
}