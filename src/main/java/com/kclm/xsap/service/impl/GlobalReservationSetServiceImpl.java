package com.kclm.xsap.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kclm.xsap.entity.TGlobalReservationSet;
import com.kclm.xsap.mapper.TGlobalReservationSetMapper;
import com.kclm.xsap.service.GlobalReservationSetService;

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


}
