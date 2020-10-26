package com.kclm.xsap.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.entity.CardCourseRecord;
import com.kclm.xsap.entity.TMemberCard;

@Mapper
public interface TMemberCardMapper extends BaseMapper<TMemberCard> {

	TMemberCard findById(Long id);
	
	List<TMemberCard> findAll();
	
	//以下方法针对中间表操作
	boolean insertMix(Long cardId,Long courseId);
	
	CardCourseRecord findBindCourse(Long cardId,Long courseId);
	
	boolean deleteBindCourse(Long cardId);
	
}