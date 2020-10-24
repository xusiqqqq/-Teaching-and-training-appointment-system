package com.kclm.xsap.entity;

import java.io.Serializable;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_global_reservation_set",resultMap = "TGlobalReservationSetMap")
/**
 *	全局预约设置在数据库的记录永远只有第一条，后续的更改操作，都是对第一条的更新
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月27日 上午3:41:58 
 * @description 此类用来描述了全局预约条件的设置
 *
 */
public class TGlobalReservationSet extends BaseEntity implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
    * 可提前预约的天数
    */
    private Integer startDay;

    /**
     * 模式1：提前预约截止天数，上课前
     */
    private Integer endDay;
    
    /**
     * 模式1：提前预约截止时间(24小时内)，上课前
     */
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    
    /**
     * 模式2：提前预约截止小时数，离上课前
     */
    private Integer endHour;
    
    /**
     * 模式1：提前预约取消的距离天数
     */
    private Integer cancelDay;
    
    /**
     * 模式1：提前预约取消的时间限制（24小时内）
     */
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime cancelTime;
    
    /**
     * 模式2：提前预约取消的距离小时数
     */
   private Integer cancelHour;
    
}