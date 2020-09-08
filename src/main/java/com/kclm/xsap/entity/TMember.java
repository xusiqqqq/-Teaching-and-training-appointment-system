package com.kclm.xsap.entity;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class TMember extends BaseEntity {

    private String name;

    private String sex;

    private String phone;

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
    private Boolean isDeleted;

}