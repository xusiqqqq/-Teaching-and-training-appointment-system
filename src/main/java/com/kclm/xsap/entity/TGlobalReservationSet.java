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
    * 可提前预约的天数
    */
    private Integer startTime;

    /**
    * 提前预约的截止时间，上课前
    */
    private LocalDateTime endTime;

    /**
    * 提前预约取消的限制时间
    */
    private LocalDateTime cancelTime;

}