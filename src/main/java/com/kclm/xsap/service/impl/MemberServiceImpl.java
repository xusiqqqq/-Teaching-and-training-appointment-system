package com.kclm.xsap.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kclm.xsap.dto.ClassRecordDTO;
import com.kclm.xsap.dto.ConsumeRecordDTO;
import com.kclm.xsap.dto.MemberCardDTO;
import com.kclm.xsap.dto.MemberDTO;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.mapper.TMemberMapper;
import com.kclm.xsap.service.MemberService;

public class MemberServiceImpl implements MemberService{

	@Autowired
	private TMemberMapper memberMapper;
	
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
	public boolean update(TMember member) {
		memberMapper.updateById(member);
		return true;
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
	public Integer findByKeyword(String condition) {
		return null;
	}

	@Override
	public boolean bindCard(TMemberBindRecord cardBind) {
		return false;
	}

	@Override
	public boolean saveByBundle(String filePath) {
		return false;
	}

	@Override
	public boolean bindByBunble(String filePath) {
		return false;
	}

	@Override
	public MemberDTO getMemberDetailById(Long id) {
		return null;
	}
	
	@Override
	public List<MemberCardDTO> findAllCardByPage(Long id) {
		return null;
	}

	@Override
	public List<ClassRecordDTO> listClassRecords(Long id) {
		return null;
	}

	@Override
	public List<ReserveRecordDTO> listReserveRecords(Long id) {
		return null;
	}

	@Override
	public List<ConsumeRecordDTO> listConsumeRecord(Long id) {
		return null;
	}

}
