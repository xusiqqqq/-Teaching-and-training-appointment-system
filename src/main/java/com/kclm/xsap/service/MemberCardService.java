package com.kclm.xsap.service;

import java.util.List;

import com.kclm.xsap.dto.MemberLogDTO;
import com.kclm.xsap.entity.TConsumeRecord;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.entity.TMemberCard;
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
	
	/**
	 * 	删除操作，实际为让卡片变为“不激活”
	 * @param id
	 * @return
	 */
	boolean deleteById(Long id);
	
	boolean update(TMemberCard card);
	
	/**
	 * 	通过id查询会员卡信息
	 * @param id
	 * @return
	 */
	TMemberCard findById(Long id);
	
	/**
	 * 	通过name查找会员卡信息
	 * @param name
	 * @return
	 */
	TMemberCard findByName(String name);
	
	/**
	 * 	查询指定会员的绑卡信息 - 单条
	 * @param memberId
	 * @param cardId
	 * @return
	 */
	TMemberBindRecord findBindRecord(Long memberId, Long cardId);
	
	/**
	 * 	查询指定会员的绑卡信息 - 全部
	 * @param memberId
	 * @return
	 */
	List<TMemberBindRecord> memberBindCardList(Long memberId);
	
	/**
	 * 	更新会员绑卡记录
	 * @param bind
	 * @return
	 */
	boolean updateBindRecord(TMemberBindRecord bind);
	
	/**
	 * 	找到某个课程绑定的所有会员卡信息
	 * @param courseId
	 * @return
	 */
	List<TMemberCard> listByCourseId(Long courseId);
	
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
	 * @param recharge 充值记录实体类
	 * @return boolean。true：充值成功；false：充值失败
	 */
	boolean recharge(TRechargeRecord recharge);
	
	
	/**
	 *  会员卡消费操作
	 * @param consume 消费记录实体类
	 * @return boolean。true：消费完成；false：消费异常
	 */
	boolean consume(TConsumeRecord consume);
	
	/**
	 * 获取当前会员卡的所有操作记录
	 * @param memberBindId 会员绑定id
	 * @return List<TMemberLog>。会员卡操作记录结果集
	 */
	List<MemberLogDTO> listOperateLog(Long memberBindId);
	
}
