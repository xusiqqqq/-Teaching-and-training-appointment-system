package com.kclm.xsap.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月17日 下午12:03:51 
 * @description 此类用来描述了会员携带的各种数据
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class MemberDTO extends BaseDTO{
	
	/**
	 *  会员名称
	 */
	private String name;
	
	/**
	 * 性别
	 */
	private String gender;
	
	/**
	 * 手机号
	 */
	private String phone;
	
	/**
	 * 出生日期
	 */
	private LocalDate birthday;
	
	/**
	 * 备注
	 */
	private String note;
	
	/**
	 * 会员卡信息
	 */
	private List<MemberCardDTO> cardMessage;
	
	/**
	 * 上课记录
	 */
	private List<ClassDTO> classRecord;
	
	/**
	 * 预约记录
	 */
	private List<ReserveDTO> reserveRecord;
	
	/**
	 * 消费记录
	 */
	private List<ConsumeDTO> consumeRecord;
	
}
