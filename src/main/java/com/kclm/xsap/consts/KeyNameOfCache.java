package com.kclm.xsap.consts;

/**
 * @author fangkai
 * @description
 * @create 2022-01-04 19:37
 */
public enum KeyNameOfCache {

    //课程表页面list的缓存信息的key    CACHE_SCHEDULE_INFO
    CACHE_SCHEDULE_INFO("SCHEDULE_INFO"),
    //统计页面会员卡统计数据的缓存信息的key  MEMBER_CARD_INFO
    CACHE_OF_MEMBER_CARD_INFO("MEMBER_CARD_INFO");

    private final String msg;

    KeyNameOfCache(String msg) {
        this.msg = msg;
    }

    public String getMsg() {return msg;}
}
