package com.kclm.xsap.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_global_reservation_set",resultMap = "TGlobalReservationSetMap")
public class TGlobalReservationSet extends BaseEntity {

    /**
    * 预约开始时间，按天算
    */
    private Integer startTime;

    /**
    * 预约截止时间
    */
    private LocalDateTime endTime;

    /**
    * 预约取消时间
    */
    private LocalDateTime cancelTime;

}