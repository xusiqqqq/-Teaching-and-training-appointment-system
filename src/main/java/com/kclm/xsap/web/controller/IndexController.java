package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kclm.xsap.entity.MemberEntity;
import com.kclm.xsap.entity.ReservationRecordEntity;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.indexStatistics.CostVo;
import com.kclm.xsap.vo.indexStatistics.IndexAddAndStreamInfoVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/index")
public class IndexController {
    @Resource
    private MemberService memberService;
    @Resource
    private RechargeRecordService rechargeService;

    @Resource
    private IndexService indexService;

    @Resource
    private MemberBindRecordService bindRecordService;

    @Resource
    private ReservationRecordService reserveService;


    @GetMapping("/x_index_home.do")
    public String togoIndex(){
        return "x_index_home";
    }

    /**
     * 首页的头部信息
     * @return
     */
    @ResponseBody
    @PostMapping("/homePageInfo.do")
    public R getHomePageInfo(){
        Map<String,Integer> map=new HashMap<>();
        map.put("totalMembers",memberService.list().size());
        map.put("activeMembers",memberService.getActiveMemberCounts(LocalDateTime.now()));
        map.put("totalReservations",reserveService.list(new LambdaQueryWrapper<ReservationRecordEntity>().eq(ReservationRecordEntity::getStatus,1)).size());
        return R.ok().put("data",map);
    }

    /**
     * 当月用户新增与流失
     * @return
     */
    @ResponseBody
    @GetMapping("/homePageInfo/statisticsOfNewAndLostPeople.do")
    public R gerNewAndLostPeople(){
        LocalDateTime currentTime=LocalDateTime.now();
        int year=currentTime.getYear();
        int month=currentTime.getMonthValue();
        int day=currentTime.getDayOfMonth();
        LocalDateTime startDay=LocalDateTime.of(year,month,1,0,0);
        LocalDateTime endDay=startDay.plusDays(1);
        IndexAddAndStreamInfoVo indexAddAndStreamInfoVo=new IndexAddAndStreamInfoVo();
        List<String> time=new ArrayList<>();
        //新增用户信息
        List<Integer> data=new ArrayList<>();
        //流失用户信息
        List<Integer> data2=new ArrayList<>();

        for(int i=1;i<=day;i++){
            time.add(String.valueOf(i));
            //添加数据
            LambdaQueryWrapper<MemberEntity> qw=new LambdaQueryWrapper<>();
            qw.eq(MemberEntity::getIsDeleted,0).between(MemberEntity::getCreateTime,startDay,endDay);
            data.add(memberService.count(qw));
            data2.add(memberService.getDeletedMemberCountsBetween(startDay,endDay));
            startDay=startDay.plusDays(1);
            endDay=endDay.plusDays(1);
        }
        indexAddAndStreamInfoVo.setTitle("统计当月的会员新增与流失");
        indexAddAndStreamInfoVo.setXname("日");
        indexAddAndStreamInfoVo.setTime(time);
        indexAddAndStreamInfoVo.setData(data);
        indexAddAndStreamInfoVo.setData2(data2);
        return R.ok().put("data",indexAddAndStreamInfoVo);
    }

    /**
     * 当前月每日收费统计statisticsOfDailyCharge
     * @return
     */
    @ResponseBody
    @GetMapping("/homePageInfo/statisticsOfDailyCharge.do")
    public R getStatisticsOfDailyCharge(){
        LocalDateTime currentTime=LocalDateTime.now();
        int year=currentTime.getYear();
        int month=currentTime.getMonthValue();
        int day=currentTime.getDayOfMonth();
        LocalDateTime startDay=LocalDateTime.of(year,month,1,0,0);
        LocalDateTime endDay=startDay.plusDays(1);
        CostVo costVo=new CostVo();
        costVo.setTitle("当前月每日收费统计");
        costVo.setXname("日");
        List<String> time=new ArrayList<>();
        List<Integer> data=new ArrayList<>();
        for (int i = 1; i <= day; i++) {
            time.add(String.valueOf(i));
            BigDecimal chargeCountsByDay = indexService.getChargeCountsByDay(startDay, endDay);
            if(chargeCountsByDay!=null){
                data.add(chargeCountsByDay.intValue());
            }else{
                data.add(0);
            }
            startDay=startDay.plusDays(1);
            endDay=endDay.plusDays(1);
        }
        costVo.setTime(time);
        costVo.setData(data);
        return R.ok().put("data",costVo);
    }

    /**
     * 会员卡占比统计
     * @return
     */
    @ResponseBody
    @PostMapping("/homePageInfo/statisticsOfMemberCard.do")
    public R getStatisticsOfMemberCard(){
        return R.ok().put("data",indexService.getCardsBindCounts());
    }

    /**
     * 用户登出
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().removeAttribute("LOGIN_USER");
        return "redirect:/user/toLogin";
    }



}
