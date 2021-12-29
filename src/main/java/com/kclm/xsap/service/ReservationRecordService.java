package com.kclm.xsap.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kclm.xsap.entity.ReservationRecordEntity;
import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.vo.ScheduleDetailReservedVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 预约记录
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
public interface ReservationRecordService extends IService<ReservationRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);


    Integer getActiveUserCount();


    List<ScheduleDetailReservedVo> getReserveList(Long id, String flag);

    List<ReservationRecordEntity> getReserveInfo(Long id);
}

