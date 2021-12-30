package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.IndexHomeDateVo;
import com.kclm.xsap.vo.indexStatistics.IndexAddAndStreamInfoVo;
import com.kclm.xsap.vo.indexStatistics.IndexPieChartVo;
import com.kclm.xsap.vo.register.RegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ConsumeRecordService consumeRecordService;

    @Autowired
    private MemberCardService memberCardService;

    @Autowired
    private MemberBindRecordService memberBindRecordService;

    @Autowired
    private EmployeeService employeeService;

    /**
     * @author fangkai
     * @date 2021/12/5 0005 12:37
     * 登录首页
     */
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("index/toRegister")
    public String toRegister() {
        return "x_register";
    }

    /**
     * 跳转 首页数据页
     *
     * @return
     */
    @GetMapping("/index/x_index_home.do")
    public String x_index_home() {
        return "x_index_home";
    }


    /**
     * 当月新增与流失人数统计
     *
     * @return 返回首页eCharts数据 折线图数据
     */
    @GetMapping("/index/homePageInfo/statisticsOfNewAndLostPeople.do")
    @ResponseBody
    public R homePage() {

        //创建要返回到前台的图表数据vo
        IndexAddAndStreamInfoVo infoVo = new IndexAddAndStreamInfoVo();

        //创建返回vo的x轴list
        List<String> xStrList = new ArrayList<>();
        //创建返回vo的y轴list
        List<Integer> yDataList = new ArrayList<>();
        //创建返回vo的y轴list
        List<Integer> yDataList2 = new ArrayList<>();

        //查询当月所有注销用户
        List<MemberEntity> allLogoutMemberInfo = memberService.getCurrentMonthLogoutMemberInfo(LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        //查询所有当月存活的用户信息
        List<MemberEntity> allSurviveMemberInfo = memberService.list(new QueryWrapper<MemberEntity>().select("id", "create_time").likeRight("create_time", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))).orderByDesc("create_time"));
        log.debug("\n==>查到的当前月的所有存活的会员信息==>{}", allSurviveMemberInfo);

        if (allSurviveMemberInfo.isEmpty()) {
            return R.error("当月没有新增用户");
        }
        if (allLogoutMemberInfo.isEmpty()) {
            return R.error("当月没有注销用户");
        }

        //创建一个  ’日期’ --> ‘新增会员数量’ 的map
        HashMap<Integer, Integer> mapOfAddMembersPerDay = new HashMap<>();
        allSurviveMemberInfo.forEach(member -> {
            int dayOfMonth = member.getCreateTime().getDayOfMonth();
            mapOfAddMembersPerDay.put(dayOfMonth, mapOfAddMembersPerDay.getOrDefault(dayOfMonth, 0) + 1);
        });

        //创建一个 ‘日期’ --> ‘注销会员数量’的map
        HashMap<Integer, Integer> mapOfLogoutMembersPerDay = new HashMap<>();
        allLogoutMemberInfo.forEach(member -> {
            int dayOfMonth = member.getLastModifyTime().getDayOfMonth();
            mapOfLogoutMembersPerDay.put(dayOfMonth, mapOfLogoutMembersPerDay.getOrDefault(dayOfMonth, 0) + 1);
        });

        //获取今天
        int today = LocalDate.now().getDayOfMonth();
        for (int i = 1; i <= today; i++) {
            //为x轴数据赋值
            xStrList.add(String.valueOf(i));
            //为y轴赋值【每日新增用户数量】
            yDataList.add(mapOfAddMembersPerDay.getOrDefault(i, 0));
            yDataList2.add(mapOfLogoutMembersPerDay.getOrDefault(i, 0));
        }

        infoVo.setTitle("当月新增与流失人数统计")
                .setXname("日")
                .setTime(xStrList)
                .setData(yDataList)
                .setData2(yDataList2);

        return R.ok().put("data", infoVo);
    }


    /**
     * 当月每日收费统计
     *
     * @return 返回首页eCharts统计数 折线图数据
     */
    @GetMapping("/index/homePageInfo/statisticsOfDailyCharge.do")
    @ResponseBody
    public R statisticsOfDailyCharge() {

        //创建要返回到前台的图表数据vo
        IndexAddAndStreamInfoVo infoVo = new IndexAddAndStreamInfoVo();

        //创建返回vo的x轴list
        List<String> xStrList = new ArrayList<>();
        //创建返回vo的y轴list
        List<Integer> yDataList = new ArrayList<>();

        //查询当月所有消费记录
        List<ConsumeRecordEntity> allConsumeRecordInfoCurrent = consumeRecordService.list(new QueryWrapper<ConsumeRecordEntity>()
                .select("money_cost", "create_time")
                .likeRight("create_time", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .orderByDesc("create_time"));

        if (allConsumeRecordInfoCurrent.isEmpty()) {
            return R.error("本月还没有消费记录");
        }

        //创建一个 '日期' --> '当日消费金额' 的map
        HashMap<Integer, Integer> currentAmountOfConsumptionMap = new HashMap<>();

        allConsumeRecordInfoCurrent.forEach(consume -> {
            //取出每次消费的金额//todo double?
            int moneyCost = consume.getMoneyCost().intValue();
            //取出每次消费的日期的日
            int dayOfMonth = consume.getCreateTime().getDayOfMonth();

            currentAmountOfConsumptionMap.put(dayOfMonth, currentAmountOfConsumptionMap.getOrDefault(dayOfMonth, 0) + moneyCost);
        });

        //今天
        int today = LocalDate.now().getDayOfMonth();
        for (int i = 1; i < today; i++) {
            xStrList.add(String.valueOf(i));
            yDataList.add(currentAmountOfConsumptionMap.getOrDefault(i, 0));
        }

        //给返回的vo赋值
        infoVo.setTitle("当月每日消费统计")
                .setXname("日")
                .setTime(xStrList)
                .setData(yDataList);

        return R.ok().put("data", infoVo);
    }


    /**
     * @return 前台eCharts 饼图数据
     */
    @PostMapping("/index/homePageInfo/statisticsOfMemberCard.do")
    @ResponseBody
    public R statisticsOfMemberCard() {

        //查出所有的会员卡模板
        List<MemberCardEntity> cardEntityList = memberCardService.list(new QueryWrapper<MemberCardEntity>().select("id", "name"));
        log.debug("\n==>打印所有会员卡模板==>{}", cardEntityList);

        if (cardEntityList.isEmpty()) {
            return R.error("还没有创建会员卡");
        }

        List<IndexPieChartVo> pieChartVos = cardEntityList.stream().map(cardEntity -> {
            //根据不同会员卡模板id查询绑定次数【即为所有会员持有的该卡的数量】
            int countOfBingCard = memberBindRecordService.count(new QueryWrapper<MemberBindRecordEntity>().eq("card_id", cardEntity.getId()));
            log.debug("\n==>打印当前会员卡模板名字==>{},==>打印当前会员卡被持有的数量==>{}", cardEntity.getName(), countOfBingCard);

            //创建eCharts所需数据格式的vo并赋值     -->  { value: 28, name: 'spring' }
            IndexPieChartVo vo = new IndexPieChartVo().setName(cardEntity.getName()).setValue(countOfBingCard);
            return vo;
        }).collect(Collectors.toList());
        log.debug("\n打印返饼图数据");
        pieChartVos.forEach(System.out::println);

        return R.ok().put("data", pieChartVos);
    }

    /**
     * 获取首页数据
     *
     * @return
     */
    @PostMapping("/index/homePageInfo.do")
    @ResponseBody
    public R homePageInfo() {


        //获取会员总数
        Integer memberCount = memberService.count();
        log.debug("会员总数count:{}", memberCount);
        //获取最近一月有约课的用户（活跃用户）
        Integer activeUserCount = reservationRecordService.getActiveUserCount();
        //获取预约总数
        Integer reserveCount = reservationRecordService.count(new QueryWrapper<ReservationRecordEntity>().eq("status", 1));

        IndexHomeDateVo indexHomeDateVo = new IndexHomeDateVo()
                .setTotalMembers(memberCount)
                .setActiveMembers(activeUserCount)
                .setTotalReservations(reserveCount);

        log.debug("返回的VO：indexHomeDateVo：{}", indexHomeDateVo);


        return R.ok().put("data", indexHomeDateVo);


    }


    /**
     * 注销登录
     *
     * @param session
     * @return
     */
    @GetMapping("/index/logout")
    public String logout(HttpSession session) {
        log.debug("\n==>用户点击销户退出...");
        session.invalidate();
//        return "redirect:/user/toLogin";
        return "x_login";
    }


    /**
     * 用户注册
     * @param registerVo 注册表单
     * @param model 返回信息
     * @return 前往登录page或者返回注册
     */
    @PostMapping("/index/register")
    public String register(RegisterVo registerVo, Model model) {
        log.debug("\n==>用户注册：打印前台传入的注册信息==>{}", registerVo);
        if (StringUtils.isNotBlank(registerVo.getUserName()) && StringUtils.isNotBlank(registerVo.getPassword())) {
            if (!registerVo.getPassword().equals(registerVo.getPwd2())) {
                model.addAttribute("CHECK_TYPE_ERROR", 1);
                return "x_register";
            } else {
                int isExistSameNameEmp = employeeService.count(new QueryWrapper<EmployeeEntity>().eq("name", registerVo.getUserName()));
                if (isExistSameNameEmp > 0) {
                    model.addAttribute("CHECK_TYPE_ERROR", 0);
                    return "x_register";
                } else {
                    EmployeeEntity employeeEntity = new EmployeeEntity()
                            .setName(registerVo.getUserName())
                            .setRolePassword(registerVo.getPassword())
                            .setCreateTime(LocalDateTime.now());
                    boolean isRegister = employeeService.save(employeeEntity);
                    log.debug("\n==>注册账户是否成功？==>{}", isRegister);
                    if (isRegister) {
                        return "redirect:/user/toLogin";
                    } else {
                        log.error("用户注册失败！");
                        return "x_register";
                    }
                }
            }
        } else {
            //改成validation //todo
            log.debug("\n==>注册表单输入不正确！null");
//            return null;
            throw new RuntimeException("注册不正确");
        }
    }

    /**
     * @return
     */
    @PostMapping("/index/report.do")
    @ResponseBody
    public R report() {

        return new R().put("reportData", null);
    }
}
