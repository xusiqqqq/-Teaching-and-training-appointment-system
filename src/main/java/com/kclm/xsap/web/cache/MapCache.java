package com.kclm.xsap.web.cache;

import com.kclm.xsap.consts.KeyNameOfCache;
import com.kclm.xsap.utils.ExpiryMap;
import com.kclm.xsap.vo.CourseScheduleVo;
import com.kclm.xsap.vo.MemberCardStatisticsWithTotalDataInfoVo;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @author fangkai
 * @description
 * @create 2022-01-04 17:14
 */
@Component("mapCache")
public class MapCache {

    //存放会员卡统计数据的信息
//    HashMap<KeyNameOfCache, MemberCardStatisticsWithTotalDataInfoVo> cacheMemberInfoMap = new HashMap<>();
    ExpiryMap<KeyNameOfCache, Object> cacheMemberInfoMap = new ExpiryMap<>();


    //存放课程表日程表数据的信息
    HashMap<KeyNameOfCache, List<CourseScheduleVo>> cacheScheduleMap = new HashMap<>();


    /**
     * 统计页面-会员卡统计缓存
     * @return
     */
    @Bean(name = "getMemberCardInfoMap")
//    public ExpiryMap<KeyNameOfCache, MemberCardStatisticsWithTotalDataInfoVo> getMemberCardInfoMap() {
    public ExpiryMap<KeyNameOfCache, Object> getCacheInfo() {
        //存放会员卡统计信息的map
        return cacheMemberInfoMap;
    }
/*
    @Bean(name = "getScheduleListInfo")
    public HashMap<KeyNameOfCache, List<CourseScheduleVo>> getScheduleListInfo() {
        return cacheScheduleMap;
    }*/

}
