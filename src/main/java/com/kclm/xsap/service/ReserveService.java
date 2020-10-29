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
	 * 	判断预约的有效性。（1，2由创建时间确定；3由修改时间确定）
	 * @param reserve
	 * @return 0，不限制任何时间；1，过早预约；2，过晚预约；3，取消预约过晚
	 */
	Integer reserveCheck(TReservationRecord reserve);
	
	/**
	 * 	共6种状态码：-2，预约超额；-1，已预约过；0，录入成功；1，预约时间过早；2，预约时间过晚，3，卡片过期
	 * @param reserve
	 * @return
	 */
	Integer save(TReservationRecord reserve);
	
	/**
	 * 	取消预约。共4种状态码：-2，预约超额；-1，预约次数达上限；0，更新成功；3，预约取消时间过晚
	 * @param reserve
	 * @return
	 */
	Integer update(TReservationRecord reserve);
	
	/**
	 * 查询一条预约记录
	 * @param reserveId
	 * @return
	 */
	TReservationRecord findOne(Long reserveId);
	
	/**
	 * 	根据会员id和排课id查询到一条预约记录
	 * @param memberId
	 * @param scheduleId
	 * @return
	 */
	TReservationRecord findOneByMS(Long memberId, Long scheduleId);
	
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
