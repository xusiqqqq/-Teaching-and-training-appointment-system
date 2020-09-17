package com.kclm.xsap.service;

import java.time.LocalDate;
import java.util.List;

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
	
	/**
	 * 分页查询。获取当前排课的预约记录
	 * @param scheduleId
	 * @param currentPage
	 * @param pageSize
	 * @return List<TReservationRecord>。预约记录结果集
	 */
	List<TReservationRecord> getReserveRecords(Integer scheduleId,Integer currentPage,Integer pageSize);
	
	/**
	 * 获取当前排课的预约记录
	 * @param scheduleId
	 * @return List<TReservationRecord>。预约记录结果集
	 */
	List<TReservationRecord> exportRecord(Integer scheduleId);

	/**
	 * 获取指定时间段的预约记录
	 * @param startDate
	 * @param endDate
	 * @return List<TReservationRecord>。预约记录结果集
	 */
	List<TReservationRecord> exportRecordList(LocalDate startDate,LocalDate endDate);
	
}
