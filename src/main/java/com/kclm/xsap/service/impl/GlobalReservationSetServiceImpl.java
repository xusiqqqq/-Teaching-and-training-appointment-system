package com.kclm.xsap.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kclm.xsap.entity.TGlobalReservationSet;
import com.kclm.xsap.mapper.TGlobalReservationSetMapper;
import com.kclm.xsap.service.GlobalReservationSetService;

@Service
@Transactional
public class GlobalReservationSetServiceImpl implements GlobalReservationSetService{

	@Autowired
	TGlobalReservationSetMapper globalMapper;
	
	@Override
	public boolean update(TGlobalReservationSet glogal) {
		globalMapper.updateById(glogal);
		return true;
	}

	@Override
	public List<TGlobalReservationSet> findAll() {
		List<TGlobalReservationSet> globalList = globalMapper.selectList(null);
		return globalList;
	}

	@Override
	public TGlobalReservationSet findOne(Long id) {
		TGlobalReservationSet getOne = globalMapper.selectById(id);
		return getOne;
	}


}
