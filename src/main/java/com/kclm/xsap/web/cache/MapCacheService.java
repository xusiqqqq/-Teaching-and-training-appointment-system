package com.kclm.xsap.web.cache;

import com.kclm.xsap.consts.KeyNameOfCache;
import com.kclm.xsap.utils.ExpiryMap;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


/**
 * @author fangkai
 * @description
 * @create 2022-01-04 17:14
 */
@Component("mapCache")
public class MapCacheService {

    //存放会员卡统计数据的信息
    ExpiryMap<KeyNameOfCache, Object> cacheMemberInfoMap = new ExpiryMap<>();

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

}
