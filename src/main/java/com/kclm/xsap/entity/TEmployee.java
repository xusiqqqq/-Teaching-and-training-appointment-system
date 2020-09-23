package com.kclm.xsap.entity;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_employee",resultMap = "TEmployeeMap")
public class TEmployee extends BaseEntity {

    private String name;

    private String phone;

    private String sex;

    private LocalDate birthday;

    /**
    * 介绍
    */
    private String introduce;

    /**
     * 员工头像路径
     */
    private String avatarUrl;
    
    private String note;

    /**
    * 操作角色名
    */
    private String roleName;

    /**
    * 操作角色密码
    */
    private String rolePassword;

    /**
    * 操作角色类型，1，超级管理员；0，普通管理员
    */
    private Integer roleType;

    /**
     *  操作角色邮箱
     */
    private String roleEmail;
    
    /**
    * 逻辑删除，1代表已删，无效；0代表未删，有效
    */
    @TableLogic
    private Integer isDeleted;

}