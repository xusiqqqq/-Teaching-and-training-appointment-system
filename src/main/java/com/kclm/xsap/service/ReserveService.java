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

	/**
	 * 	共5种状态码：-2，预约超额；-1，已预约过；0，录入成功；1，预约时间过早；2，预约时间过晚
	 * @param reserve
	 * @return
	 */
	Integer save(TReservationRecord reserve);
	
	/**
	 * 	共4种状态码：-2，预约超额；-1，预约次数达上限；0，更新成功；3，预约取消时间过晚
	 * @param reserve
	 * @return
	 */
	Integer update(TReservationRecord reserve);
	
	//查询一条预约记录
	TReservationRecord findOne(Long reserveId);
	
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
