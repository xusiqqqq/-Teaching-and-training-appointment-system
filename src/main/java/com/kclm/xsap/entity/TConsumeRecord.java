package com.kclm.xsap.entity;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TConsumeRecord extends BaseEntity {

	/**
    * 关联的会员
    */
    private List<TMember> members;
	
    /**
    * 操作类型
    */
    private String operateType;

    /**
    * 卡次变化
    */
    private Integer cardCountChange;

    /**
    * 有效天数变化
    */
    private Integer cardDayChange;

    /**
    * 操作员
    */
    private String operator;

    private String note;

}