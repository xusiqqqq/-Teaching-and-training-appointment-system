package com.kclm.xsap.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TReservationRecord;

@Mapper
public interface TReservationRecordMapper extends BaseMapper<TReservationRecord> {

	/**
	 * 	排课记录的已预约记录
	 * @param scheduleId
	 * @return
	 */
	List<ReserveRecordDTO> listReservedView(Long scheduleId);

	/**
	 * 	排课记录的全部预约记录
	 * @param scheduleId
	 * @return
	 */
	List<ReserveRecordDTO> listReserveRecordView(Long scheduleId);
	
	
}