package com.kclm.xsap.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月17日 下午3:27:47 
 * @description 此类用来描述了会员卡信息
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class MemberCardDTO{

	/**
	 * 	对应绑定记录的id
	 */
	private Long bindCardId;
	/**
	 * 会员卡名称
	 */
	private String name;
	
	/**
	 * 卡片类型
	 */
	private String type;
	
	/**
	 * 总可用次数
	 */
    private Integer totalCount;
    
    /**
          * 到期时间
     */
    private LocalDateTime dueTime;
    
    /**
     * 	当前会员绑定的某一张会员卡的激活状态（针对这一个会员）
     */
    private Integer activeStatus;
	
}
