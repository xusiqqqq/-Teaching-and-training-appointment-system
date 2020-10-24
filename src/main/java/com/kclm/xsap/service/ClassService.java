package com.kclm.xsap.service;

import com.kclm.xsap.entity.TClassRecord;
import com.kclm.xsap.entity.TReservationRecord;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月26日 下午4:10:16 
 * @description 此类用来描述了上课管理业务，用来保存上课记录
 *
 */
public interface ClassService {

	/**
	 * 	更新上课记录的”预约检定“状态
	 * @param reserve
	 * @return
	 */
	boolean reserveClassSet(TReservationRecord reserve);
	
	/**
	 * 	根据预约情况，进行上课记录的单个录入
	 * @param reserve
	 * @return
	 */
	boolean saveByReserve(TReservationRecord reserve);
	
	/**
	 * 	针对“已预约”，进行全部录入（考虑不周，暂且停用）
	 * @return
	 */
	@Deprecated
	boolean saveAll();
	
	/**
	 * 	单个上课记录确认
	 * @param classId	上课记录id
	 * @param status	上课记录确认状态
	 * @return
	 */
	boolean update(Long classId,Integer status);
	
	/**
	 * 	针对“未确认”上课，进行全部上课记录的更新，暂且用不上
	 * @return
	 */
	boolean updateAll();
	
	/**
	 * 	删除单个上课记录
	 * @param classId
	 * @return
	 */
	boolean deleteOne(Long classId);
	
	/**
	 * 	根据id查找上课记录
	 * @param classId
	 * @return
	 */
	TClassRecord findById(Long classId);
	
	/**
	 * 根据当前排课id，针对“未确认”上课，进行全部上课记录的更新
	 * @param scheduleId
	 * @return
	 */
	boolean ensureByScheduleId(Long scheduleId);
	
	/**
	 * 	查找”已预约“记录对应的上课记录为”已确认“的一条数据 - 方法1
	 * @param memberId	会员id
	 * @param scheduleId	排课id
	 * @return
	 */
	TClassRecord findIsEnsure(Long memberId, Long scheduleId);
	
	/**
	 * 	查找”已预约“记录对应的上课记录为”已确认“的一条数据 - 方法2
	 * @param classId
	 * @return
	 */
	TClassRecord findIsEnsureById(Long classId);
}
