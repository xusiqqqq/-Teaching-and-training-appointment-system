package com.kclm.xsap.service;

import java.util.List;

import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.ConsumeRecordDTO;
import com.kclm.xsap.dto.MemberCardDTO;
import com.kclm.xsap.dto.MemberDTO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TMemberBindRecord;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月15日 上午11:18:38 
 * @description 此类用来描述了会员管理业务
 *
 */
public interface MemberService {
	
	boolean save(TMember member);
	
	boolean deleteById(Long id);
	
	boolean update(TMember member);
	
	/**
	 * 保存查询出的会员详情信息
	 * @param id
	 * @return MemberDTO
	 */
	MemberDTO getMemberDetailById(Long id);
	
	
	/**
	 *  获取所有会员信息
	 * @return List<TMember>。会员信息的结果集
	 */
	List<TMember> findAll();
	
	/**
	 *  分页查询。获取所有会员信息
	 * @param currentPage 当前页码
	 * @param pageSize 每页展示数据个数
	 * @return List<TMember>。会员信息的结果集
	 */
	List<TMember> findAllByPage(Integer currentPage,Integer pageSize);
	
	/**
	 * 根据条件搜索匹配的会员
	 * @param condition 搜索条件
	 * @return List<TMember>。匹配到的会员集合
	 */
	List<TMember> findByKeyword(String condition);
	
	/**
	 *  绑定会员卡
	 * @param cardBind 会员绑定记录id
	 * @return boolean。true：绑定成功；false：绑定失败
	 */
	boolean bindCard(TMemberBindRecord cardBind);
	
	/**
	 *  批量导入会员
	 * @param filePath 文件上传路径 
	 * @return boolean。true：导入成功；false：导入失败
	 */
	boolean saveByBundle(String filePath);
	
	/**
	 *  批量绑定会员卡
	 * @param filePath 文件上传路径
	 * @return boolean。true：绑定成功；false：绑定失败
	 */
	boolean bindByBunble(String filePath);
	
	/**
	 * 当前会员绑定的所有会员卡信息
	 * @param id 会员id
	 * @return List<MemberCardDTO>。会员绑定记录结果集
	 */
	List<MemberCardDTO> findAllCardRecords(Long id);

	/**
	 * 当前会员的上课记录(当预约状态为“已预约”时，则此预约记录表示上课记录)
	 * @param id 会员id
	 * @return List<ClassRecordDTO>。上课记录结果集
	 */
	List<ClassRecordDTO> listClassRecords(Long id);
	
	/**
	 * 当前会员的预约记录
	 * @param id 会员id
	 * @return List<ReserveRecordDTO>。预约记录结果集
	 */
	List<ReserveRecordDTO> listReserveRecords(Long id);
	
	/**
	 * 当前会员的消费记录
	 * @param id 会员id
	 * @return List<ConsumeRecordDTO>。消费记录结果集
	 */
	List<ConsumeRecordDTO> listConsumeRecords(Long id);
	
	
}
