package com.kclm.xsap.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.kclm.xsap.entity.TMemberLog;

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
public class MemberCardDTO extends BaseDTO{

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
	 * 激活状态。1，已激活；0，未激活
	 */
	private Integer status;
	
}
