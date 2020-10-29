package com.kclm.xsap.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.ConsumeRecordDTO;
import com.kclm.xsap.dto.MemberCardDTO;
import com.kclm.xsap.dto.MemberVO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.entity.TMemberLog;
import com.kclm.xsap.mapper.TMemberBindRecordMapper;
import com.kclm.xsap.mapper.TMemberLogMapper;
import com.kclm.xsap.mapper.TMemberMapper;
import com.kclm.xsap.service.MemberService;

@Service
@Transactional
public class MemberServiceImpl implements MemberService{

//	@Autowired
//	private MemberConvert memberConvert;

//	@Autowired
//	private MemberCardConvert memberCardConvert;
//
//	@Autowired
//	private ReserveRecordConvert reserveRecordConvert;
//
//	@Autowired
//	private ConsumeRecordConvert consumeRecordConvert;

//	@Autowired
//	private ClassRecordConvert classRecordConvert;

	@Autowired
	private TMemberMapper memberMapper;
	
	@Autowired
	private TMemberBindRecordMapper bindMapper;
	
	@Autowired
	private TMemberLogMapper logMapper;
	
	@Override
	public boolean save(TMember member) {
		memberMapper.insert(member);
		return true;
	}

	@Override
	public boolean deleteById(Long id) {
		memberMapper.deleteById(id);
		return true;
	}

	@Override
	public TMember update(TMember member) {
		memberMapper.updateById(member);
		TMember mber = memberMapper.selectById(member.getId());;
		return mber;
	}

	@Override
	public List<TMember> findAll() {
		List<TMember> memberList = memberMapper.selectList(null);
		return memberList;
	}

	@Override
	public List<TMember> findAllByPage(Integer currentPage, Integer pageSize) {
		IPage<TMember> page = new Page<>(currentPage,pageSize);
		IPage<TMember> pageList = memberMapper.selectPage(page , null);
		return pageList.getRecords();
	}

	@Override
	public TMember findByPhone(String phone) {
		TMember member = memberMapper.selectOne(new QueryWrapper<TMember>().eq("phone", phone));
		return member;
	}
	
	@Override
	public List<TMember> findByKeyword(String condition) {
		List<TMember> memberList = memberMapper.selectList(new QueryWrapper<TMember>()
				.eq("phone", condition).or().like("name", condition));
		return memberList;
	}

	@Override
	public boolean bindCard(TMemberBindRecord cardBind) {
		bindMapper.insert(cardBind);
		//操作记录
		TMemberLog log = new TMemberLog();
		log.setMemberBindId(cardBind.getId());
		log.setType("绑卡操作");
		log.setOperator("系统处理");
		log.setInvolveMoney(cardBind.getReceivedMoney());
		logMapper.insert(log );
		return true;
	}

	/* 待处理  - begin*/
	
	//文件读取，存库
	@Override
	public boolean saveByBundle(String filePath) {
		return false;
	}
	
	//文件读取，存库
	@Override
	public boolean bindByBunble(String filePath) {
		return false;
	}

	/* 待处理  - end*/
	
	//匹配视图的会员信息
	@Override
	public List<MemberVO> listMemberView(){
		List<MemberVO> memberVoList = memberMapper.listMemberView();
		for (MemberVO memberVo : memberVoList) {
			memberVo.setNamePhone( memberVo.getName() + "(" + memberVo.getPhone() + ")");
		}
		return memberVoList;
	}
	
	//会员卡信息
	@Override
	public List<MemberCardDTO> listCardRecords(Long id) {
		System.out.println("---------- 会员卡信息 --------");
		List<MemberCardDTO> cardDtoList = memberMapper.listMemberCardView(id);
		for (MemberCardDTO cardDto : cardDtoList) {
			//会员卡到期日
			cardDto.setDueTime(cardDto.getDueTime().plusDays(cardDto.getTotalCount()));
		}
		return cardDtoList;
	}

	//上课记录
	@Override
	public List<ClassRecordDTO> listClassRecords(Long id) {
		System.out.println("---------- 上课记录 --------");
		List<ClassRecordDTO> classDtoList = memberMapper.listClassView(id);
		for (ClassRecordDTO classRecordDTO : classDtoList) {
			classRecordDTO.setClassTime(LocalDateTime.of(classRecordDTO.getStartDate(), classRecordDTO.getStartTime()));
		}
		return classDtoList;
	}

	//预约记录
	//跟上课记录不同的地方在于，预约状态不限制，预约的会员卡仅能一次预约一门课，一门课在被预约的状态下，同一个会员不能二次预约
	@Override
	public List<ReserveRecordDTO> listReserveRecords(Long id) {
		System.out.println("---------- 预约记录 --------");
		List<ReserveRecordDTO> reserveDtoList = memberMapper.listReserveView(id);
		return reserveDtoList;
	}

	//消费记录
	@Override
	public List<ConsumeRecordDTO> listConsumeRecords(Long id) {
		System.out.println("---------- 消费记录 --------");
		List<ConsumeRecordDTO> consumeDtoList = memberMapper.listConsumeView(id);
		return consumeDtoList;
	}

	//根据id查询
	@Override
	public TMember getMember(Long id) {
		TMember member = memberMapper.selectById(id);
		return member;
	}
}
