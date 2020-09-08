package com.kclm.xsap.entity;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TMemberLog extends BaseEntity {

	/**
    * 关联的会员
    */
    private List<TMember> members;
	
    /**
    * 操作类型
    */
    private String type;

    /**
    * 操作员名称
    */
    private String operator;

}