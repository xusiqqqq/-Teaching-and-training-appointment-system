package com.kclm.xsap.entity;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_member",resultMap = "TMemberMap")
public class TMember extends BaseEntity implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;

    private String sex;

    private String phone;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
    * 备注信息
    */
    private String note;

    /**
    * 用户头像路径
    */
    private String avatarUrl;

    /**
    * 用户的逻辑删除，1有效，0无效
    */
    @TableLogic
    private Integer isDeleted;

}