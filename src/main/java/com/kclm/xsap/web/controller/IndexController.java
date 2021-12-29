package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.entity.ReservationRecordEntity;
import com.kclm.xsap.service.MemberService;
import com.kclm.xsap.service.ReservationRecordService;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.IndexHomeDateVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author fangkai
 * @description
 * @create 2021-12-05 12:17
 */
@Slf4j
@Controller
public class IndexController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ReservationRecordService reservationRecordService;


    /**
     *
     * @author fangkai
     * @date 2021/12/5 0005 12:37
     * 登录首页
     */
    @GetMapping({"","/index"})
    public String index() {
        return "index";
    }


    /**
     * 跳转 首页数据页
     * @return
     */
    @GetMapping("/index/x_index_home.do")
    public String x_index_home() {
        return "x_index_home";
    }

    /**
     * 返回首页统计数据 TODO 这段写的乱七八糟
     * @param startDateStr
     * @param endDateStr
     * @return
     */
    @GetMapping("/index/homePage.do")
    @ResponseBody
    public IndexHomeDateVo homePage(String startDateStr, String endDateStr) {
        log.debug("startDateStr:{},endDateStr{}",startDateStr,endDateStr);

        if (!StringUtils.isEmpty(startDateStr) || !StringUtils.isEmpty(endDateStr) || endDateStr.compareTo(startDateStr) >= 0) {

            //查询每日约课数量预约
            //查询所有有效的约课记录
            List<ReservationRecordEntity> statusIs1List = reservationRecordService.list(new QueryWrapper<ReservationRecordEntity>().select("create_time").eq("status", 1));
            statusIs1List.forEach(System.out::println);

            ArrayList<String> allDateList = new ArrayList<>();

            //取出记录中的日期记录并格式化为yyyy-MM-dd，添加进allDateList
            statusIs1List.forEach(item -> {
                String date = item.getCreateTime().toString().substring(0, 10);
                allDateList.add(date);
            });

            /*
              思路：首先对日期进行格式化(取年月日)后去重，在计算每个日期的数量；
             */
            //创建一个Hashmap
            HashMap<String, Integer> dateWithCountMap = new HashMap<>();
            //这不麻烦？？？？？

            //去重，
            for (String date : allDateList) {
                dateWithCountMap.put(date, dateWithCountMap.containsKey(date) ? dateWithCountMap.get(date) + 1 : 1);
            }
            log.debug("dateWithCountMap:{}",dateWithCountMap);
            //TODO 太不优雅了

            Set<String> ketSet = dateWithCountMap.keySet();
            String[] dateList = ketSet.toArray(new String[0]);
            Collection<Integer> values = dateWithCountMap.values();
            Integer[] dailyReservations = values.toArray(new Integer[0]);


            IndexHomeDateVo indexDate = new IndexHomeDateVo()
                    .setDateList(dateList)
                    .setDailyReservations(dailyReservations);
            log.debug("首页表格返回的VO：indexDate:{}", indexDate);

            return indexDate;

        } else {
            log.error("日期选择不正确！");
        }

        return null;
    }



    @PostMapping("/index/homePage.do")
    @ResponseBody
    public IndexHomeDateVo homePage() {


        //获取会员总数
        Integer memberCount = memberService.count();
        log.debug("会员总数count:{}",memberCount);
        //获取最近一月有约课的用户（活跃用户）
        Integer activeUserCount = reservationRecordService.getActiveUserCount();
        //获取预约总数
        Integer reserveCount = reservationRecordService.count(new QueryWrapper<ReservationRecordEntity>().eq("status", 1));

        IndexHomeDateVo indexHomeDateVo = new IndexHomeDateVo()
                .setTotalMembers(memberCount)
                .setActiveMembers(activeUserCount)
                .setTotalReservations(reserveCount);

        log.debug("返回的VO：indexHomeDateVo：{}",indexHomeDateVo);



        return indexHomeDateVo;

    }


    @PostMapping("/index/report.do")
    @ResponseBody
    public R report() {

        return new R().put("reportData", null);
    }
}
