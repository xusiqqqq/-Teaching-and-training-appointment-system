package com.kclm.xsap.service;

import java.util.List;

import com.kclm.xsap.entity.TConsumeRecord;
import com.kclm.xsap.entity.TMemberCard;
import com.kclm.xsap.entity.TMemberLog;
import com.kclm.xsap.entity.TRechargeRecord;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月15日 下午2:14:15 
 * @description 此类用来描述了会员卡管理业务
 *
 */
public interface MemberCardService {

	boolean save(TMemberCard card);
	
	boolean deleteById(Integer id);
	
	boolean update(TMemberCard card);
	
	/**
	 *  获取所有的会员卡信息
	 * @return List<TMemberCard>。会员卡信息结果集
	 */
	List<TMemberCard> findAll();
	
	/**
	 * 分页查询。获取所有的会员卡信息
	 * @param currentPage 当前页码
	 * @param pageSize 每页展示数据个数
	 * @return List<TMemberCard>。会员卡信息结果集
	 */
	List<TMemberCard> findAllByPage(Integer currentPage,Integer pageSize);
	
	/**
	 *  会员卡充值操作
	 * @param cardId 会员卡id
	 * @param recharge 充值记录实体类
	 * @return boolean。true：充值成功；false：充值失败
	 */
	boolean recharge(Integer cardId,TRechargeRecord recharge);
	
	/**
	 *  会员卡消费操作
	 * @param cardId 会员卡id
	 * @param consume 消费记录实体类
	 * @return boolean。true：消费完成；false：消费异常
	 */
	boolean consume(Integer cardId,TConsumeRecord consume);
	
	/**
	 * 分页查询。获取当前会员卡的所有操作记录
	 * @param cardId 会员卡id
	 * @param currentPage 当前页码
	 * @param pageSize 每页展示数据个数
	 * @return List<TMemberLog>。会员卡操作记录结果集
	 */
	List<TMemberLog> listOperateLog(Integer cardId,Integer currentPage,Integer pageSize);
	
}
