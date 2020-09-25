package com.kclm.xsap.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_member_card",resultMap = "TMemberCardMap")
public class TMemberCard extends BaseEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 关联的课程
	 */
	@TableField(exist = false)
    @ToString.Exclude
	private List<TCourse> courseList;
	
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
    private Integer status;

}