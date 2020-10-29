package com.kclm.xsap.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.ConsumeRecordDTO;
import com.kclm.xsap.dto.MemberCardDTO;
import com.kclm.xsap.dto.MemberVO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TMember;

@Mapper
public interface TMemberMapper extends BaseMapper<TMember> {

	TMember findById(Long id);
	
	/**
	 *	拿到memberVO所需的数据 
	 * @return
	 */
	List<MemberVO> listMemberView();
	
	/**
	 * 	会员卡信息
	 * @param memberId
	 * @return
	 */
	List<MemberCardDTO> listMemberCardView(Long memberId);
	
	/**
	 * 	预约记录
	 * @param memberId
	 * @return
	 */
	List<ReserveRecordDTO> listReserveView(Long memberId);

	/**
	 * 	上课记录
	 * @param memberId
	 * @return
	 */
	List<ClassRecordDTO> listClassView(Long memberId);
	
	/**
	 * 	消费记录
	 * @param memberId
	 * @return
	 */
	List<ConsumeRecordDTO> listConsumeView(Long memberId);
	
}