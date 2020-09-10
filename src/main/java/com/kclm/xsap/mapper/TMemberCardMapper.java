package com.kclm.xsap.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.TMemberCard;

@Mapper
public interface TMemberCardMapper extends BaseMapper<TMemberCard> {

	TMemberCard findById(Integer id);
	
	List<TMemberCard> findAll();
}