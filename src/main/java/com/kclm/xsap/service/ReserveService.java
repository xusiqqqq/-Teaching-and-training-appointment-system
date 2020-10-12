package com.kclm.xsap.service;

import java.time.LocalDate;
import java.util.List;

import com.kclm.xsap.dto.ReserveRecordDTO;
import com.kclm.xsap.entity.TReservationRecord;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月15日 下午4:10:16 
 * @description 此类用来描述了预约管理业务
 *
 */
public interface ReserveService {

	boolean save(TReservationRecord reserve);
	
	boolean update(TReservationRecord reserve);
	
	/**
	 * 获取当前排课的已预约记录
	 * @param scheduleId 排课记录id
	 * @return List<ReserveRecordDTO>。已预约记录结果集
	 */
	List<ReserveRecordDTO> listReserved(Long scheduleId);
	
	/**
	 * 获取当前排课的预约记录
	 * @param scheduleId 排课记录id
	 * @return List<ReserveRecordDTO>。预约记录结果集
	 */
	List<ReserveRecordDTO> listReserveRecords(Long scheduleId);
	
	/**
	 * 导出当前排课的预约记录
	 * @param scheduleId 排课记录id
	 * @return List<ReserveRecordDTO>。预约记录结果集
	 */
	List<ReserveRecordDTO> listExportRecord(Long scheduleId);

	/**
	 * 获取指定时间段的预约记录
	 * @param startDate 起始日期
	 * @param endDate 结束日期
	 * @return List<ReserveRecordDTO>。预约记录结果集
	 */
	List<ReserveRecordDTO> listExportRecordRange(LocalDate startDate,LocalDate endDate);
	
}
