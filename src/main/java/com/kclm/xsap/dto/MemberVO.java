package com.kclm.xsap.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年10月17日 下午2:35:40 
 * @description 此类用来匹配会员概览视图数据
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class MemberVO {
	
	private Long id;
	/**
	 *  会员名称（手机号）
	 */
	private String namePhone;
	
	/**
	 * 性别
	 */
	private String gender;
	
	/**
	 * 出生日期
	 */
	private LocalDate birthday;
	
	/**
	 * 备注
	 */
	private String note;
	
	/**
	 * 持有的会员卡
	 */
	private String cardHold;
	
}
