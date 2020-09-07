package com.kclm.xsap.entity;

import java.util.Date;
import lombok.Data;

@Data
public class TEmployee extends BaseEntity {

    private String name;

    private String phone;

    private String sex;

    private Date birthday;

    /**
    * 介绍
    */
    private String introduce;

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
    private Boolean roleType;

    /**
    * 逻辑删除，1有效，0无效
    */
    private Boolean isDeleted;

}