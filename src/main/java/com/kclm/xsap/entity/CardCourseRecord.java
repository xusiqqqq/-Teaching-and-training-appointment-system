package com.kclm.xsap.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName(value = "t_course_card")
public class CardCourseRecord {

	/**
	 * 	会员卡id
	 */
	private Long cardId;

	/**
	 * 	课程id
	 */
	private Long courseId;

}
