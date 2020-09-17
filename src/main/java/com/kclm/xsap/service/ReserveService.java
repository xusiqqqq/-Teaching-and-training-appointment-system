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
	 * @param scheduleId 排课记录id
	 * @param currentPage 当前页码
	 * @param pageSize 每页展示数据个数
	 * @return List<TReservationRecord>。预约记录结果集
	 */
	List<TReservationRecord> listReserveRecords(Integer scheduleId,Integer currentPage,Integer pageSize);
	
	/**
	 * 获取当前排课的预约记录
	 * @param scheduleId 排课记录id
	 * @return List<TReservationRecord>。预约记录结果集
	 */
	List<TReservationRecord> listExportRecord(Integer scheduleId);

	/**
	 * 获取指定时间段的预约记录
	 * @param startDate 起始日期
	 * @param endDate 结束日期
	 * @return List<TReservationRecord>。预约记录结果集
	 */
	List<TReservationRecord> listExportRecordRange(LocalDate startDate,LocalDate endDate);
	
}
