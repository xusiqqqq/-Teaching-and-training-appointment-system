package com.kclm.xsap.entity;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TMemberCard extends BaseEntity {

	/**
	 * 关联的课程
	 */
	private List<TCourse> courses;
	
    private String name;
    
    /**
     * 单价
     */
    private BigDecimal price;

    /**
    * 描述信息
    */
    private String desc;

    /**
    * 备注信息
    */
    private String note;

    /**
    * 会员卡类型
    */
    private String type;

    /**
    * 总可用次数
    */
    private Integer totalCount;

    /**
    * 总可用天数
    */
    private Integer totalDay;

    /**
    * 激活状态，1激活，0非激活
    */
    private Boolean status;

}