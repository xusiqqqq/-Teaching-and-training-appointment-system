package com.kclm.xsap.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月17日 上午11:50:12 
 * @description 此类用来描述了首页关于报表的数据
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ReportDTO extends BaseDTO{

	/**
	 * 会员卡种类名称+会员卡绑定数量
	 */
	private Map<String,Integer> memberCardBindingMap;
	
}
