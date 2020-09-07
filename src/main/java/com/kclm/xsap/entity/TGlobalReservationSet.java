package com.kclm.xsap.entity;

import java.util.Date;
import lombok.Data;

@Data
public class TGlobalReservationSet extends BaseEntity {
    private Long id;

    /**
    * 预约开始时间，按天算
    */
    private Integer startTime;

    /**
    * 预约截止时间
    */
    private Date endTime;

    /**
    * 预约取消时间
    */
    private Date cancelTime;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 修改时间
    */
    private Date lastModifyTime;

    /**
    * 版本
    */
    private Integer version;
}