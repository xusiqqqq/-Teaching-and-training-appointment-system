package com.kclm.xsap.entity;

import java.util.Date;
import lombok.Data;

@Data
public class TMemberLog extends BaseEntity {

    /**
    * 操作类型
    */
    private String type;

    /**
    * 操作员名称
    */
    private String operator;

    /**
    * 会员id
    */
    private Long memberId;

}