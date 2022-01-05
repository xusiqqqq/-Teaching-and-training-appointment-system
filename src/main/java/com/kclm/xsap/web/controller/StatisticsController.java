package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.consts.KeyNameOfCache;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.ExpiryMap;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.MemberCardStatisticsVo;
import com.kclm.xsap.vo.MemberCardStatisticsWithTotalDataInfoVo;
import com.kclm.xsap.vo.statistics.CardCostVo;
import com.kclm.xsap.vo.statistics.ClassCostVo;
import com.kclm.xsap.vo.statistics.StatisticsOfCardCostVo;
import com.kclm.xsap.web.cache.MapCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fangkai
 * @description
 * @create 2021-12-15 13:01
 */

@Controller("statisticsController")
@Slf4j
@RequestMapping("/statistics")
public class StatisticsController {

    //会员卡统计数据缓存的key
    private static final String CACHE_OF_MEMBER_CARD_INFO = "MEMBER_CARD_INFO";


    //代表老师上的课的时间的可能的最大月份
    private static final Long MAX_MONTH = -11111L;
    //代表老师上的课的时间的可能的最大季度
    private static final Long MAX_QUARTER = -22222L;

    //遍历次数记数,用于当所有老师都遍历完后如果还没有老师的排课信息时的标记
    int TRAVERSE_COUNT = 0;

    @Resource
    private MemberBindRecordService memberBindRecordService;

    @Resource(name = "memberService")
    private MemberService memberService;

    @Resource
    private RechargeRecordService rechargeRecordService;

    @Resource
    private ConsumeRecordService consumeRecordService;

    @Resource
    private ScheduleRecordService scheduleRecordService;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private CourseService courseService;

    @Resource
    private MapCache mapCache;

    /**
     * 跳转到会员卡统计页面
     *
     * @return x_card_list_stat.html
     */
    @GetMapping("/x_card_list_stat.html")
    public String togoCardListStat() {
        return "statistics/x_card_list_stat";
    }


    /**
     * 跳转到收费统计页面
     *
     * @return x_card_cost_stat.html
     */
    @GetMapping("/x_card_cost_stat.html")
    public String togoCardCostStat() {
        return "statistics/x_card_cost_stat";
    }

    /**
     * 跳转到课消统计页面
     *
     * @return x_class_cost_stat.html
     */
    @GetMapping("/x_class_cost_stat.html")
    public String togoClassCostStat() {
        return "statistics/x_class_cost_stat";
    }


    /**
     * 跳转到总课时统计页面
     *
     * @return x_class_hour_stat.html
     */
    @GetMapping("/x_class_hour_stat.html")
    public String togoClassHourStat() {
        return "statistics/x_class_hour_stat";
    }

    /**
     * 跳转到新增与流失统计页面
     *
     * @return x_member_num_static.html
     */
    @GetMapping("/x_member_num_static.html")
    public String togoMemberNumStat() {
        return "statistics/x_member_num_static";
    }


    //数据统计模块

    /**
     * 获取数据统计模块-会员卡统计json
     *
     * @return r -> json
     */
    @PostMapping("/cardInfo.do")
    @ResponseBody
    public R memberCardInfo() {
        /* 会员卡统计
        总课次：将所有充值记录中的充值次数加起来，   总金额：
        已用课次：消费记录中消费次数加起来          医用金额
        剩余：相减或者查bind表                   剩余金额
         */
        //todo 重写这个方法！！！太慢了！！用map缓存？

        //使用map做本地缓存
//        HashMap<KeyNameOfCache, MemberCardStatisticsWithTotalDataInfoVo> CACHE_MEMBER_CARD_INFO_MAP = mapCache.getMemberCardInfoMap();
        ExpiryMap<KeyNameOfCache, Object> CACHE_MEMBER_CARD_INFO_MAP = mapCache.getCacheInfo();
//        MemberCardStatisticsWithTotalDataInfoVo cacheMemberCardInfo = CACHE_MEMBER_CARD_INFO_MAP.get(KeyNameOfCache.CACHE_OF_MEMBER_CARD_INFO);
        Object cacheValueOfMapForMemberCardInfo = CACHE_MEMBER_CARD_INFO_MAP.get(KeyNameOfCache.CACHE_OF_MEMBER_CARD_INFO);
        MemberCardStatisticsWithTotalDataInfoVo cacheMemberCardInfo = null;
        if(cacheValueOfMapForMemberCardInfo instanceof MemberCardStatisticsWithTotalDataInfoVo)  {
            log.debug("会员卡数据vo类型instanceof匹配-->强转成MemberCardStatisticsWithTotalDataInfoVo返回到前台");
            cacheMemberCardInfo = (MemberCardStatisticsWithTotalDataInfoVo) cacheValueOfMapForMemberCardInfo;
        }
        if (null != cacheMemberCardInfo) {
            log.debug("\n==>返回map的值==>{}", cacheMemberCardInfo);
            return R.ok().put("data", cacheMemberCardInfo);
        }

        long start = System.currentTimeMillis();

        //查出所有可以使用的会员卡
        List<MemberBindRecordEntity> allMemberWithCard = memberBindRecordService.list(new QueryWrapper<MemberBindRecordEntity>().select("id","member_id","valid_count").eq("active_status", 1));

        if (allMemberWithCard.isEmpty()) {
            return R.error("还没有会员绑卡信息");
        }
        List<MemberCardStatisticsVo> memberCardStatisticsVos = allMemberWithCard.stream().map(entity -> {
            //会员id
            Long memberId = entity.getMemberId();
            log.debug("\n==>会员id==>{}", memberId);
            //获取会员名
            String memberName = memberService.getOne(new QueryWrapper<MemberEntity>().select("name").eq("id", memberId)).getName();
            //获取会员所持有的会员卡号【即绑定id】
            Long bindCardId = entity.getId();
            //获取该会员卡下的所有充值记录
            List<RechargeRecordEntity> allRechargeRecordOfCurrentCard = rechargeRecordService.list(new QueryWrapper<RechargeRecordEntity>().select("add_count","received_money").eq("member_bind_id", bindCardId));
            //获取该会员卡下的所有消费记录
            List<ConsumeRecordEntity> allConsumeRecordOfCurrentCard = consumeRecordService.list(new QueryWrapper<ConsumeRecordEntity>().select("card_count_change","money_cost").eq("member_bind_id", bindCardId));

            //获取总课次：    1.获取该会员卡下的所有充值记录,2.获取每次充值次数,3.相加求总次数(卡自带的次数已经添加到充值记录了)
            int totalClassTimes = allRechargeRecordOfCurrentCard.stream().mapToInt(RechargeRecordEntity::getAddCount).sum();
//            List<Integer> allAddClassTimes = allRechargeRecordOfCurrentCard.stream().map(RechargeRecordEntity::getAddCount).collect(Collectors.toList());
            //获取已用课次    1.获取该会员卡下的所有消费记录,2.获取每次消费的次数,3.相加求已用次数
            int usedClassTimes = allConsumeRecordOfCurrentCard.stream().mapToInt(ConsumeRecordEntity::getCardCountChange).sum();
            //获取剩余课次
            Integer remainingClassTimes = entity.getValidCount();
            //获取总额      1.获取该会员卡下的所有充值记录,2.获取每次充值金额, 3.相加求总金额
            BigDecimal lumpSumBigD = allRechargeRecordOfCurrentCard.stream().map(RechargeRecordEntity::getReceivedMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
            //获取已用金额    1.获取该会员卡下的所有消费记录, 2.获取每次消费的金额, 3.相加求已用金额
            BigDecimal amountUsedBigD = allConsumeRecordOfCurrentCard.stream().map(ConsumeRecordEntity::getMoneyCost).reduce(BigDecimal.ZERO, BigDecimal::add);
            //获取剩余金额
            BigDecimal balanceBigD = memberBindRecordService.getById(bindCardId).getReceivedMoney();

            //获取中文货币符号
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.CHINA);
            //构造器创建中文货币符号的format
            DecimalFormat format = new DecimalFormat("¤00.00", symbols);

            return new MemberCardStatisticsVo()
                    .setMemberId(memberId)
                    .setMemberName(memberName)
                    .setBindCardId(bindCardId)
                    .setTotalClassTimes(totalClassTimes)
                    .setUsedClassTimes(usedClassTimes)
                    .setRemainingClassTimes(remainingClassTimes)
                    .setLumpSumBigD(lumpSumBigD)
                    .setAmountUsedBigD(amountUsedBigD)
                    .setBalanceBigD(balanceBigD)
                    .setLumpSum(format.format(lumpSumBigD))
                    .setAmountUsed(format.format(amountUsedBigD))
                    .setBalance(format.format(balanceBigD));
        }).collect(Collectors.toList());
        log.debug("\n==>后台封装的用于数据统计页面的会员卡统计表单的list==>{}", memberCardStatisticsVos);
        long middle = System.currentTimeMillis();
        log.debug("\n==>middle==>{}", middle-start);
        //继续处理获取饼图数据

        //总课时
        int totalCourseTimeAll = 0;
        //总已用课时
        int usedCourseTimeAll = 0;
        //总剩余课时
        int remainCourseTimeAll = 0;
        //总额
        BigDecimal totalMoneyAll = BigDecimal.ZERO;
        //总已用金额
        BigDecimal usedMoneyAll = BigDecimal.ZERO;
        //总剩余金额
        BigDecimal remainMoneyAll = BigDecimal.ZERO;


        for (MemberCardStatisticsVo memberCardStatisticsVo : memberCardStatisticsVos) {
            int totalClassTimes = memberCardStatisticsVo.getTotalClassTimes();
            totalCourseTimeAll += totalClassTimes;
            int usedClassTimes = memberCardStatisticsVo.getUsedClassTimes();
            usedCourseTimeAll += usedClassTimes;
            int remainingClassTimes = memberCardStatisticsVo.getRemainingClassTimes();
            remainCourseTimeAll += remainingClassTimes;
            BigDecimal lumpSumBigD = memberCardStatisticsVo.getLumpSumBigD();
            totalMoneyAll = totalMoneyAll.add(lumpSumBigD);
            BigDecimal amountUsedBigD = memberCardStatisticsVo.getAmountUsedBigD();
            usedMoneyAll = usedMoneyAll.add(amountUsedBigD);
            BigDecimal balanceBigD = memberCardStatisticsVo.getBalanceBigD();
            remainMoneyAll = remainMoneyAll.add(balanceBigD);
        }


       /*
        //---------------
        //总课时
        int totalCourseTimeAll = memberCardStatisticsVos.stream().mapToInt(MemberCardStatisticsVo::getTotalClassTimes).sum();
        //总已用课时
        int usedCourseTimeAll = memberCardStatisticsVos.stream().mapToInt(MemberCardStatisticsVo::getUsedClassTimes).sum();
        //总剩余课时
        int remainCourseTimeAll = memberCardStatisticsVos.stream().mapToInt(MemberCardStatisticsVo::getRemainingClassTimes).sum();
        //总额
        BigDecimal totalMoneyAll = memberCardStatisticsVos.stream().map(MemberCardStatisticsVo::getLumpSum).reduce(BigDecimal.ZERO, BigDecimal::add);
        //总已用金额
        BigDecimal usedMoneyAll = memberCardStatisticsVos.stream().map(MemberCardStatisticsVo::getAmountUsed).reduce(BigDecimal.ZERO, BigDecimal::add);
        //总剩余金额
        BigDecimal remainMoneyAll = memberCardStatisticsVos.stream().map(MemberCardStatisticsVo::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
*/

        MemberCardStatisticsWithTotalDataInfoVo memberCardStatisticsWithTotalDataInfoVo = new MemberCardStatisticsWithTotalDataInfoVo()
                .setMemberCardStatisticsVos(memberCardStatisticsVos)
                .setTotalCourseTimeAll(totalCourseTimeAll)
                .setUsedCourseTimeAll(usedCourseTimeAll)
                .setRemainCourseTimeAll(remainCourseTimeAll)
                .setTotalMoneyAll(totalMoneyAll)
                .setUsedMoneyAll(usedMoneyAll)
                .setRemainMoneyAll(remainMoneyAll);

        log.info("\n==>后台封装的用于数据统计页面的完整信息==>{}", memberCardStatisticsWithTotalDataInfoVo);
        long end = System.currentTimeMillis();
        log.debug("\n==>end==>{}", (end-start));

        //添加本地缓存        10分钟后自动过期
        CACHE_MEMBER_CARD_INFO_MAP.put(KeyNameOfCache.CACHE_OF_MEMBER_CARD_INFO, memberCardStatisticsWithTotalDataInfoVo,1000*60*10);

        return R.ok().put("data", memberCardStatisticsWithTotalDataInfoVo);
    }


    /**
     * 查找所有消费记录的所在年份并去重
     *
     * @return r List<Integer> yearList
     */
    @PostMapping("/yearList")
    @ResponseBody
    public R yearList() {
        //查出消费记录的年份
        List<Integer> yearList = consumeRecordService.list(new QueryWrapper<ConsumeRecordEntity>().select("create_time")).stream().map(ConsumeRecordEntity::getCreateTime).map(LocalDateTime::getYear).distinct().collect(Collectors.toList());
        log.debug("\n==>打印查出的所有消费记录所在年份==>{}", yearList);
        return R.ok().put("data", yearList);
    }


    /**
     * 数据统计--收费统计page的数据返回
     *
     * @param vo 传入的统计模式相关数据
     * @return r -> 对应模式的响应数据
     */
    @PostMapping("/cardCostMonthOrSeasonOrYear")
    @ResponseBody
    public R cardCostInfo(StatisticsOfCardCostVo vo) {
        log.debug("\n==>打印收费统计page传入的时间信息封装==>{}", vo);

        Integer unit = vo.getUnit();
        Integer yearOfSelect = vo.getYearOfSelect();
        Integer beginYear = vo.getBeginYear();
        Integer endYear = vo.getEndYear();

        //创建要返回到前台的vo
        CardCostVo cardCostVo = new CardCostVo();

        //查出所有消费数据按时间倒序
        List<ConsumeRecordEntity> consumeInfoWithTimeAndMoney = consumeRecordService.list(new QueryWrapper<ConsumeRecordEntity>()
                .select("money_cost", "create_time")
                .orderByDesc("create_time"));

        //过滤出月统计模式和季度统计模式---根据指定年份查出所有消费记录的时间和消费金额
        List<ConsumeRecordEntity> consumeInfoWithTimeAndMoneyForMonthAndQuarter = consumeInfoWithTimeAndMoney.stream()
                .filter(consume -> consume.getCreateTime().getYear() == yearOfSelect).collect(Collectors.toList());

        if (consumeInfoWithTimeAndMoneyForMonthAndQuarter.isEmpty()) {
            return R.error("选中统计时段没有消费记录");
        }


        //创建一个盛放月份和当月花费的map
        HashMap<Integer, Integer> monthCostMap = new HashMap<>();
        //创建一个盛放季度和季度消费的map
        HashMap<Integer, Integer> quarterCostMap = new HashMap<>();
        for (ConsumeRecordEntity recordEntity : consumeInfoWithTimeAndMoneyForMonthAndQuarter) {
            //得出月份
            int monthValue = recordEntity.getCreateTime().getMonthValue();
            //map.put(monthValue, (map.get(monthValue) == null ? 0 : map.get(monthValue)) + recordEntity.getMoneyCost().intValue());
            monthCostMap.put(monthValue, monthCostMap.getOrDefault(monthValue, 0) + recordEntity.getMoneyCost().intValue());

            //根据月份得出季度
            int quarter = (monthValue - 1) / 3 + 1;
            quarterCostMap.put(quarter, quarterCostMap.getOrDefault(quarter, 0) + recordEntity.getMoneyCost().intValue());
        }

        //查出选中的年有数据的最大的月份【降序取第一个】
        int maxMonth = consumeInfoWithTimeAndMoneyForMonthAndQuarter.get(0).getCreateTime().getMonthValue();
        //创建返回vo的x轴list
        List<String> xStrList = new ArrayList<>();
        //创建返回vo的y轴list
        List<Integer> yDataList = new ArrayList<>();

        //按月统计【按选中的年份到本月或者1~12月进行统计】
        if (unit == 1) {
            //添加数据
            for (int i = 1; i <= maxMonth; i++) {
                Integer data = monthCostMap.getOrDefault(i, 0);
                xStrList.add(i + "月份");
                yDataList.add(data);
            }
            //设置属性
            cardCostVo.setTitle("月收费模式")
                    .setXname("月")
                    .setTime(xStrList).
                    setData(yDataList);
            log.debug("\n==>打印收费统计页面按月统计图表的x轴数据==>{}\n==>打印统计收费页面按月统计图表的Y轴数据==>{}", xStrList, yDataList);
        } else if (unit == 2) {
            //查出选中年有数据的最大季度
            int maxQuarter = (maxMonth - 1) / 3 + 1;

            //添加数据
            for (int i = 1; i <= maxQuarter; i++) {
                Integer data = quarterCostMap.getOrDefault(i, 0);
                xStrList.add("第" + i + "季度");
                yDataList.add(data);
            }
            //设置vo属性
            cardCostVo.setTitle("季度收费模式")
                    .setXname("季度")
                    .setTime(xStrList)
                    .setData(yDataList);

            log.debug("\n==>打印收费统计页面按季度统计图表的x轴数据==>{}\n==>打印统计收费页面按月统计图表的Y轴数据==>{}", xStrList, yDataList);
        } else {

            if (endYear < beginYear) {
                return R.error("起始时间与结束时间冲突！请修改后再查看统计结果");
            }

            //过滤出年度统计模式---根据指定范围年份查出所有消费记录的时间和消费金额
            List<ConsumeRecordEntity> consumeInfoWithTimeAndMoneyForYear = consumeInfoWithTimeAndMoney.stream().filter(consume -> consume.getCreateTime().getYear() >= beginYear && consume.getCreateTime().getYear() <= endYear)
                    .collect(Collectors.toList());

            //创建一个盛放年份和年消费的map
            HashMap<Integer, Integer> yearCostMap = new HashMap<>();
            for (ConsumeRecordEntity recordEntity : consumeInfoWithTimeAndMoneyForYear) {
                int year = recordEntity.getCreateTime().getYear();
                yearCostMap.put(year, yearCostMap.getOrDefault(year, 0) + recordEntity.getMoneyCost().intValue());
            }

            //添加数据
            for (int i = beginYear; i <= endYear; i++) {
                Integer data = yearCostMap.getOrDefault(i, 0);
                xStrList.add(i + "年");
                yDataList.add(data);
            }
            //设置vo属性
            cardCostVo.setTitle("年度收费模式")
                    .setXname("年")
                    .setTime(xStrList)
                    .setData(yDataList);

            log.debug("\n==>打印收费统计页面按年度统计图表的x轴数据==>{}\n==>打印统计收费页面按月统计图表的Y轴数据==>{}", xStrList, yDataList);
        }


        return R.ok().put("data", cardCostVo);
    }


    /**
     * 数据统计--课消统计的返回信息
     *
     * @param vo 前端传入统计模式及相关数据
     * @return r -> 对应模式的响应数据
     */
    @PostMapping("/classCostMonthOrSeasonOrYear")
    @ResponseBody
    public R classCostInfo(StatisticsOfCardCostVo vo) {
        /*
        1.查出所有的老师（id）
        2.通过id查该老师在此时刻之前的所有排课信息（限制统计时间）
        3.根据排课信息的courseId（继续查单次课程消耗次数） ,order_nums（这个在添加的时候还没有处理好，先不用），

         */
        log.debug("\n==>打印收费统计page传入的时间信息封装==>{}", vo);

        //取出相关数据
        Integer unit = vo.getUnit();
        Integer yearOfSelect = vo.getYearOfSelect();
        Integer beginYear = vo.getBeginYear();
        Integer endYear = vo.getEndYear();

        //创建要返回到前台的vo
        ClassCostVo classCostVo = new ClassCostVo();

        /*
        其实目的只是查出某老师在统计时间单位内的课消次数
         */
        //查出所有有效的老师id和名字
        List<EmployeeEntity> teacherInfoWithIdAndName = employeeService.list(new QueryWrapper<EmployeeEntity>().select("id", "name").eq("is_deleted", 0));
        log.debug("\n==>打印所有存在【未被删除】的老师的信息【只查询id,name】==>{}", teacherInfoWithIdAndName);

        if (teacherInfoWithIdAndName.isEmpty()) {
            return R.error("没有老师信息");
        }

        //查询出所有已经上了的课（通过超过当前时间过滤）的排课信息【只查询了courseId,teacherId,orderNums,startDate】,按照startDate倒序【为了取出最大月份或季度从而确定柱状图的最大项】
        List<ScheduleRecordEntity> allClassAlreadyTaken = scheduleRecordService.list(new QueryWrapper<ScheduleRecordEntity>().select("course_id", "teacher_id", "order_nums", "start_date", "class_time").orderByDesc("start_date")).stream().filter(schedule -> {
            LocalDateTime classLocalDateTime = LocalDateTime.of(schedule.getStartDate(), schedule.getClassTime());
            return classLocalDateTime.isBefore(LocalDateTime.now());
        }).collect(Collectors.toList());

        log.debug("\n==>测试打印指定时间内已经上了的课程==>{}", allClassAlreadyTaken);

        if (allClassAlreadyTaken.isEmpty()) {
            return R.error("指定时间内没有上课信息");
        }


        //创建一个map存放不同老师(id)对应的在不同统计条件下的课消次数,即在mapT中存放另一个mapTeacherOne:mapT = {key = teacherId, value = map={key = 统计条件, value = 该条件下的课消次数}}
        //HashMap<Long, Object> mapT = new HashMap<>();
//        HashMap<Long, HashMap<Integer, Integer>> mapT = new HashMap<>();
        HashMap<Long, Object> mapT = new HashMap<>();


        //遍历老师信息==> 获取每个老师的排课记录==> 获取每个老师的课消信息
        for (EmployeeEntity teacher : teacherInfoWithIdAndName) {

            //过滤出当前老师在选中的年份中【按月统计或按季度统计模式】已经上过的课的排课信息(因为每次请求都是重新发的，所以没必要提取出公共部分，使用同一个流更节省性能)
            List<ScheduleRecordEntity> classAlreadyTakenForCurrentTeacherForMonthOrQuarter = allClassAlreadyTaken.stream().filter(schedule -> schedule.getTeacherId().equals(teacher.getId()) && schedule.getStartDate().getYear() == yearOfSelect).collect(Collectors.toList());

            //在这里创建map，随遍历次数变化的表示不同的老师的信息
            HashMap<Integer, Integer> mapTeacherCurrent = new HashMap<>();  //这个相当于存放了当前遍历的老师的月份的次数

            if (unit == 1) {
                //如果前台选择的统计模式是按月统计：
                /*获取当前老师上的当前课程的课消次数；由于使用月份作为map的key,所以相同开课月份的不同课程的课消次数都会被加在一起；遍历完所有课程之后即获取了当前老师的所有课消次数
                 */
                classAlreadyTakenForCurrentTeacherForMonthOrQuarter.forEach(scheduleRecordEntity -> {
                    //查出当前记录的上课时间的月份
                    int monthValue = scheduleRecordEntity.getStartDate().getMonthValue();
                    log.debug("\n==>打印当前排课的上课日期的月份==>{}", monthValue);
                    //单位课程次数
                    Integer timesCost = courseService.getById(scheduleRecordEntity.getCourseId()).getTimesCost();
                    //课程预约人数
                    Integer orderNums = scheduleRecordEntity.getOrderNums();
                    log.debug("\n==>打印当前课程单位人数所需次数==>{}\n 打印当前课程的预约人数==>{}", timesCost, orderNums);
                    mapTeacherCurrent.put(monthValue, mapTeacherCurrent.getOrDefault(monthValue, 0) + timesCost * orderNums);

                    log.debug("\n==>打印当前的老师id=> {}==>及其对应的mapTeacherCurrent【key=月份,value=课消次数】=>{}", scheduleRecordEntity.getTeacherId(), mapTeacherCurrent);
                    //将上课时间的月份最大的月份放进去,以MAX_MONTH存放//避免魔法值
                    mapT.put(MAX_MONTH, monthValue > (int) mapT.getOrDefault(MAX_MONTH, 1) ? monthValue : mapT.get(MAX_MONTH));
                });
                //以老师id作为key, 以另一个map(以月份作为key,对应课消次数作为value)做为value放进mapT
                mapT.put(teacher.getId(), mapTeacherCurrent);
            } else if (unit == 2) {
                //按季度统计
                classAlreadyTakenForCurrentTeacherForMonthOrQuarter.forEach(scheduleRecordEntity -> {
                    //取得当前记录上课时间的季度
                    int quarter = (scheduleRecordEntity.getStartDate().getMonthValue() - 1) / 3 + 1;
                    log.debug("\n==>打印当前排课的上课日期的季度==>{}", quarter);
                    //单位课程次数
                    Integer timesCost = courseService.getById(scheduleRecordEntity.getCourseId()).getTimesCost();
                    //课程预约人数
                    Integer orderNums = scheduleRecordEntity.getOrderNums();
                    log.debug("\n==>打印当前课程单位人数所需次数==>{}\n 打印当前课程的预约人数==>{}", timesCost, orderNums);

                    mapTeacherCurrent.put(quarter, mapTeacherCurrent.getOrDefault(quarter, 0) + timesCost * orderNums);
                    //将所有上过的课程的时间的最大季度放进map,并以MAX_QUARTER存放
                    mapT.put(MAX_QUARTER, quarter > (int) mapT.getOrDefault(MAX_QUARTER, 1) ? quarter : mapT.get(MAX_QUARTER));

                    log.debug("\n==>打印当前的老师id=> {}及其对应的mapTeacherCurrent【key=季度,value=课消次数】=>{}", scheduleRecordEntity.getTeacherId(), mapTeacherCurrent);
                });
                //以老师id作为key, 以另一个map(以季度作为key,对应课消次数作为value)做为value放进mapT
                mapT.put(teacher.getId(), mapTeacherCurrent);
            } else {
                //按起始和结束年份统计        //todo 要不要将流合并

                //当表单输入的时间冲突时
                if (endYear < beginYear) {
                    return R.error("起始时间与结束时间冲突！请修改后再查看统计结果");
                }

                //如果遍历完所有老师后依然没有排课记录
                if (TRAVERSE_COUNT > allClassAlreadyTaken.size()) {
                    return R.error("指定时间内没有排课记录");
                }

                List<ScheduleRecordEntity> classAlreadyTakenForCurrentTeacherForYear = allClassAlreadyTaken.stream().filter(schedule -> schedule.getTeacherId().equals(teacher.getId()) && schedule.getStartDate().getYear() >= beginYear && schedule.getStartDate().getYear() <= endYear).collect(Collectors.toList());
                log.debug("\n==>d打印测试起始结束年份的所有当前老师的上课记录==>{}", classAlreadyTakenForCurrentTeacherForYear);


                if (classAlreadyTakenForCurrentTeacherForYear.isEmpty()) {
                    TRAVERSE_COUNT++;
                    break;
                }

                classAlreadyTakenForCurrentTeacherForYear.forEach(scheduleRecordEntity -> {
                    //取得当前上课记录的上课时间的年份
                    int year = scheduleRecordEntity.getStartDate().getYear();
                    log.debug("\n==>打印当前排课记录的上课日期的年份==>{}", year);

                    //单位课程次数
                    Integer timesCost = courseService.getById(scheduleRecordEntity.getCourseId()).getTimesCost();
                    //课程预约人数
                    Integer orderNums = scheduleRecordEntity.getOrderNums();
                    log.debug("\n==>打印当前课程单位人数所需次数==>{}\n 打印当前课程的预约人数==>{}", timesCost, orderNums);

                    mapTeacherCurrent.put(year, mapTeacherCurrent.getOrDefault(year, 0) + timesCost * orderNums);
                    log.debug("\n==>打印当前的老师id=> {}及其对应的mapTeacherCurrent【key=年份,value=课消次数】=>{}", scheduleRecordEntity.getTeacherId(), mapTeacherCurrent);
                });
                //以老师id作为key, 以另一个map(以年度作为key,对应课消次数作为value)做为value放进mapT
                mapT.put(teacher.getId(), mapTeacherCurrent);

            }
        }

        /*
        创建一个类继承HashMap<Integer,Integer> 可用于强制类型转化时的instanceof，因为instanceof无法直接判断是否是泛型，此时我们的关于这个hashmap的一些逻辑就需要
        写在创建的这个类类里面了；
        todo：...
         */
        class TestClass01 extends HashMap<Integer, Integer> {
        }

        log.debug("\n==>打印保存【mapT={key=teacherId, value=mapTeacherCurrent(key=统计时间单位,value=课消次数)】==>{}", mapT);

        //获取当前mapT的key的set集合：即teacherId,进而查出对应的老师名字;因为不同统计时间下的老师可能不同，所以没有直接使用开头查出的所有老师
        Set<Long> teacherSet = mapT.keySet();
        if (teacherSet.isEmpty()) {
            return R.error("选中时间内没有老师信息");
        }
        //获取老师name
        List<String> teacherNames = employeeService.listByIds(teacherSet).stream().map(EmployeeEntity::getName).collect(Collectors.toList());

        //创建返回vo的x轴list
        List<String> xStrList = new ArrayList<>();
        //创建返回vo的y轴list
        ArrayList<List<Integer>> yDataList = new ArrayList<>();

        if (unit == 1) {

            //取出最大月份
            int maxMonth = (int) mapT.get(MAX_MONTH);
            log.debug("\n==>打印最大月份==>{}", maxMonth);
            //取出后需要删除这个键值对，不然后面遍历所有老师记录没有这条会报错
            mapT.remove(MAX_MONTH);

            //为y轴数据赋值
            for (Long teacherId : teacherSet) {
                //创建一个list存放该员工所有统计时间单位下的课消次数
                ArrayList<Integer> classCosts = new ArrayList<>();
                for (int i = 1; i <= maxMonth; i++) {
                    //应当使用instanceof判断再强制类型转化
                    //if (mapT.get(teacherId) instanceof TestClass01) {

                    HashMap<Integer, Integer> monthAndClassCost = (HashMap<Integer, Integer>) mapT.get(teacherId);
                    //HashMap<Integer, Integer> monthAndClassCost = (TestClass01) mapT.get(teacherId);
                    Integer classCost = monthAndClassCost.getOrDefault(i, 0);
                    classCosts.add(classCost);
                }
                log.debug("\n==>classCostsTest==>{}", classCosts);
                //将这个list放进vo的y轴数据（查看示例json)
                yDataList.add(classCosts);
            }
            //为x轴数据赋值
            for (int i = 1; i <= maxMonth; i++) {
                xStrList.add("第" + i + "月");
            }
            classCostVo.setTitle("老师课时消费月统计").setXname("月");

        } else if (unit == 2) {

            //取出最大的月份
            Integer maxQuarter = (Integer) mapT.get(MAX_QUARTER);
            log.debug("\n==>最大的季度时：==>{}", maxQuarter);
            //取出存放最大季度的映射后需要删除掉这个键，不然后面在遍历老师id时，没有这个key的老师会报错   //思路二：用另一个map单独存放最大的月份或季度  //不要有魔法值
            mapT.remove(MAX_QUARTER);

            //为y轴数据赋值
            teacherSet.forEach(teacherId -> {
                //创建一个list存放该员工所有统计时间单位下的课消次数
                ArrayList<Integer> classCosts = new ArrayList<>();
                for (int i = 1; i <= maxQuarter; i++) {
                    //todo instanceof
                    HashMap<Integer, Integer> monthAndClassCost = (HashMap<Integer, Integer>) mapT.get(teacherId);

                    Integer classCost = monthAndClassCost.getOrDefault(i, 0);
                    classCosts.add(classCost);
                }
                yDataList.add(classCosts);
            });
            //为x轴数据赋值
            for (int i = 1; i <= maxQuarter; i++) {
                xStrList.add("第" + i + "季度");
            }
            classCostVo.setTitle("老师课时消费季度统计").setXname("季度");

        } else {
            //为y轴数据赋值
            teacherSet.forEach(teacherId -> {
                //创建一个list存放该员工所有统计时间单位下的课消次数
                ArrayList<Integer> classCosts = new ArrayList<>();
                for (int i = beginYear; i <= endYear; i++) {
                    HashMap<Integer, Integer> monthAndClassCost = (HashMap<Integer, Integer>) mapT.get(teacherId);
                    Integer classCost = monthAndClassCost.getOrDefault(i, 0);
                    classCosts.add(classCost);
                }
                yDataList.add(classCosts);
            });
            //为x轴数据赋值
            for (int i = beginYear; i <= endYear; i++) {
                xStrList.add(i + "年");
            }
            classCostVo.setTitle("老师课时消费年统计").setXname("年度");
        }


        classCostVo.setTname(teacherNames)
                .setTime(xStrList)
                .setData(yDataList);

        log.debug("\n==> 打印课消统计页面按月统计图表的老师名list==>{}\n==>x轴时间数据==> {},\n==>y轴课消次数数据==>{}", teacherNames, xStrList, yDataList);

        return R.ok().put("data", classCostVo);
    }


    /**
     * 返回数据统计页面--总课时统计页面的数据
     *
     * @param vo 前台传入的统计模式表单
     * @return r -> 页面数据
     */
    @PostMapping("/classCountMonthOrSeasonOrYear")
    @ResponseBody
    public R classHourInfo(StatisticsOfCardCostVo vo) {
        log.debug("\n==>打印课时数统计page传入的时间信息封装==>{}", vo);

        Integer unit = vo.getUnit();
        Integer yearOfSelect = vo.getYearOfSelect();
        Integer beginYear = vo.getBeginYear();
        Integer endYear = vo.getEndYear();

        //创建要返回到前台的vo
        CardCostVo cardCountVo = new CardCostVo();

        if (unit == 1) {
            //创建返回vo的x轴list
            List<String> xStrList = new ArrayList<>();
            //创建返回vo的y轴list
            List<Integer> yDataList = new ArrayList<>();

            //创建一个盛放月份和当月一共课次数的map
            HashMap<Integer, Integer> monthClassHourMap = new HashMap<>();

            //查出所有指定年份的所有排课记录【包含id，课程id，预约人数，上课日期】,按日期倒序
            List<ScheduleRecordEntity> scheduleRecordForSpecifyYear = scheduleRecordService.list(new QueryWrapper<ScheduleRecordEntity>().select("id", "course_id", "order_nums", "start_date").likeRight("start_date", yearOfSelect).orderByDesc("start_date"));
            log.debug("\n==>打印指定年份的排课信息【包含id，课程id，预约人数，上课日期】==>");

            if (scheduleRecordForSpecifyYear.isEmpty()) {
                return R.error("指定时间内没有排课记录");
            }

            scheduleRecordForSpecifyYear.forEach(System.out::println);
            scheduleRecordForSpecifyYear.forEach(schedule -> {
                //获取当前流中的上课记录的所在月份
                int monthValue = schedule.getStartDate().getMonthValue();
                if (schedule.getOrderNums() <= 0) {
                    monthClassHourMap.put(monthValue, monthClassHourMap.getOrDefault(monthValue, 0));
                } else {
                    CourseEntity currentCourseOfSchedule = courseService.getOne(new QueryWrapper<CourseEntity>().select("times_cost").eq("id", schedule.getCourseId()));
                    Integer classTimesForMonth = monthClassHourMap.getOrDefault(monthValue, 0) + currentCourseOfSchedule.getTimesCost() * schedule.getOrderNums();
                    monthClassHourMap.put(monthValue, classTimesForMonth);
                }

            });
            log.debug("\n==>打印月份和当月一共课次数的map：monthClassHourMap==>{}", monthClassHourMap);

            //获取指定年份的所有记录中的最大月份【时间倒序取第一个】
            int maxMonth = scheduleRecordForSpecifyYear.get(0).getStartDate().getMonthValue();
            for (int i = 1; i <= maxMonth; i++) {
                Integer data = monthClassHourMap.getOrDefault(i, 0);
                xStrList.add("第" + 1 + "月");
                yDataList.add(data);
            }
            //设置属性
            cardCountVo.setTitle("月课时数统计")
                    .setXname("月")
                    .setTime(xStrList).
                    setData(yDataList);
            log.debug("\n==>打印总课时统计页面按月统计图表的x轴数据==>{}\n==>打印统计总课时页面按月统计图表的Y轴数据==>{}", xStrList, yDataList);
        } else if (unit == 2) {
            //由于每次点击都是单独发请求，提取出来反而浪费性能
            //创建返回vo的x轴list
            List<String> xStrList = new ArrayList<>();
            //创建返回vo的y轴list
            List<Integer> yDataList = new ArrayList<>();

            //创建一个盛放月份和当月一共课次数的map
            HashMap<Integer, Integer> quarterClassHourMap = new HashMap<>();
            //查出所有指定年份的所有排课记录【包含id，课程id，预约人数，上课日期】,按日期倒序
            List<ScheduleRecordEntity> scheduleRecordForSpecifyYear = scheduleRecordService.list(new QueryWrapper<ScheduleRecordEntity>().select("id", "course_id", "order_nums", "start_date").likeRight("start_date", yearOfSelect).orderByDesc("start_date"));
            log.debug("\n==>打印指定年份的排课信息【包含id，课程id，预约人数，上课日期==>");

            if (scheduleRecordForSpecifyYear.isEmpty()) {
                return R.error("指定时间内没有排课记录");
            }

            //scheduleRecordForSpecifyYear.forEach(System.out::println);
            scheduleRecordForSpecifyYear.forEach(schedule -> {
                //获取当前流中记录的上课时间所在的季度
                int quarter = (schedule.getStartDate().getMonthValue() - 1) / 3 + 1;
                if (schedule.getOrderNums() <= 0) {
                    quarterClassHourMap.put(quarter, quarterClassHourMap.getOrDefault(quarter, 0));
                } else {
                    CourseEntity currentCourseOfSchedule = courseService.getOne(new QueryWrapper<CourseEntity>().select("times_cost").eq("id", schedule.getCourseId()));
                    Integer classTimesForMonth = quarterClassHourMap.getOrDefault(quarter, 0) + currentCourseOfSchedule.getTimesCost() * schedule.getOrderNums();
                    quarterClassHourMap.put(quarter, classTimesForMonth);
                }
            });
            log.debug("\n==>打印季度和当前季度一共课次数的map：quarterClassHourMap==>{}", quarterClassHourMap);

            //取出选中年份中的记录的最大季度
            int maxQuarter = (scheduleRecordForSpecifyYear.get(0).getStartDate().getMonthValue() - 1) / 3 + 1;
            //赋值
            for (int i = 1; i <= maxQuarter; i++) {
                xStrList.add("第" + i + "季度");
                Integer data = quarterClassHourMap.getOrDefault(i, 0);
                yDataList.add(data);
            }
            //设置属性
            cardCountVo.setTitle("季度课时数统计")
                    .setXname("季度")
                    .setTime(xStrList).
                    setData(yDataList);
            log.debug("\n==>打印总课时统计页面按季度统计图表的x轴数据==>{}\n==>打印统计总课时页面按季度统计图表的Y轴数据==>{}", xStrList, yDataList);
        } else {

            //当统计时间冲突时
            if (endYear < beginYear) {
                return R.error("起始时间与结束时间冲突！请修改后再查看统计结果");
            }

            //创建返回vo的x轴list
            List<String> xStrList = new ArrayList<>();
            //创建返回vo的y轴list
            List<Integer> yDataList = new ArrayList<>();

            //创建一个盛放月份和当月一共课次数的map
            HashMap<Integer, Integer> yearClassHourMap = new HashMap<>();
            //查出所有开始年份和结束年份之间的所有排课记录【包含id，课程id，预约人数，上课日期】,按日期倒序
            List<ScheduleRecordEntity> scheduleRecordForBeginYearAndEndYear = scheduleRecordService.list(new QueryWrapper<ScheduleRecordEntity>()
                    .select("id", "course_id", "order_nums", "start_date")
                    .orderByDesc("start_date")
                    .ge("start_date", LocalDate.of(beginYear, 1, 1))
                    .le("start_date", LocalDate.of(endYear, 12, 31)));
            log.debug("\n==>打印指定年份的排课信息【包含id，课程id，预约人数，上课日期==>");
            scheduleRecordForBeginYearAndEndYear.forEach(System.out::println);

            if (scheduleRecordForBeginYearAndEndYear.isEmpty()) {
                return R.error("指定时间内没有排课记录");
            }

            //遍历
            scheduleRecordForBeginYearAndEndYear.forEach(schedule -> {
                //获取当前流中记录的上课时间所在的季度
                int year = schedule.getStartDate().getYear();
                //当该课程没有预约人数时表示这节课没有预约,
                if (schedule.getOrderNums() <= 0) {
                    yearClassHourMap.put(year, yearClassHourMap.getOrDefault(year, 0));
                } else {
                    CourseEntity currentCourseOfSchedule = courseService.getOne(new QueryWrapper<CourseEntity>().select("times_cost").eq("id", schedule.getCourseId()));
                    Integer classTimesForMonth = yearClassHourMap.getOrDefault(year, 0) + currentCourseOfSchedule.getTimesCost() * schedule.getOrderNums();
                    yearClassHourMap.put(year, classTimesForMonth);
                }
            });
            //赋值
            for (int i = beginYear; i <= endYear; i++) {
                xStrList.add("第" + i + "年度");
                Integer data = yearClassHourMap.getOrDefault(i, 0);
                yDataList.add(data);
            }
            //设置属性
            cardCountVo.setTitle("年课时数统计")
                    .setXname("年")
                    .setTime(xStrList).
                    setData(yDataList);
            log.debug("\n==>打印总课时统计页面按年度统计图表的x轴数据==>{}\n==>打印统计总课时页面按年度统计图表的Y轴数据==>{}", xStrList, yDataList);
        }

        return R.ok().put("data", cardCountVo);
    }


    /**
     * 新增与流失统计页面数据
     *
     * @param vo 前台传入的表单数据
     * @return r -> 页面数据
     */
    @PostMapping("/addAndStreamCountMonthOrSeasonOrYear")
    @ResponseBody
    public R addAndStreamInfo(StatisticsOfCardCostVo vo) {
        log.debug("\n==>新增与流失人数统计page传入的时间信息封装==>{}", vo);

        Integer unit = vo.getUnit();
        Integer yearOfSelect = vo.getYearOfSelect();
        Integer beginYear = vo.getBeginYear();
        Integer endYear = vo.getEndYear();


        //创建要返回到前台的vo
        CardCostVo cardCountVo = new CardCostVo();

        //创建返回vo的x轴list
        List<String> xStrList = new ArrayList<>();
        //创建返回vo的y轴list
        List<Integer> yDataList = new ArrayList<>();
        //创建返回vo的y轴list
        List<Integer> yDataList2 = new ArrayList<>();

        //让你不打备注！想不起来了吧！！大概是避免前端传入数据错误避免报错？
        if (null == yearOfSelect) {
            yearOfSelect = -1;
        }

        //创建要给map用于表示最大的统计时间单位
        HashMap<Long, Integer> maxStatisticalTime = new HashMap<>();

        if (unit == 1) {

            //所有还存活的会员  //.eq("is_deleted", 0) mp会默认加上
            List<MemberEntity> memberSurviveForMonthList = memberService.list(new QueryWrapper<MemberEntity>().select("create_time").likeRight("create_time", yearOfSelect).orderByDesc("create_time"));
            log.debug("\n==>打印指定年份里所有还存活的会员信息==>{}", memberSurviveForMonthList);

            //所有注销的员工   //注意逻辑删除后的数据mp查不出来
            List<MemberEntity> memberLogOutForMonthList = memberService.getMemberLogOutInSpecifyYear(yearOfSelect);
            log.debug("\n==>打印指定年份里所有注销的会员信息==>{}", memberLogOutForMonthList);

            if (memberLogOutForMonthList.isEmpty() && memberSurviveForMonthList.isEmpty()) {
                return R.error("选中时间没有新增会员和注销会员信息");
            }


            //创建一个盛放月份和当月新增会员数量的map
            HashMap<Integer, Integer> memberSurviveMonthMap = new HashMap<>();
            //创建一个盛放月份和当月注销会员数量的map
            HashMap<Integer, Integer> memberLogOutMonthMap = new HashMap<>();

            //存活的会员信息
            memberSurviveForMonthList.forEach(member -> {
                //获取该会员的入会时间的月份
                int monthValue = member.getCreateTime().getMonthValue();
                memberSurviveMonthMap.put(monthValue, memberSurviveMonthMap.getOrDefault(monthValue, 0) + 1);
                //将最大的月份放进map
                maxStatisticalTime.put(MAX_MONTH, monthValue > maxStatisticalTime.getOrDefault(MAX_MONTH, 1) ? monthValue : maxStatisticalTime.get(MAX_MONTH));
            });
            //注销的会员信息
            memberLogOutForMonthList.forEach(member -> {
                //获取该会员的注销时间的月份
                int monthValue = member.getLastModifyTime().getMonthValue();
                memberLogOutMonthMap.put(monthValue, memberLogOutMonthMap.getOrDefault(monthValue, 0) - 1);
                //将最大的月份放进map
                maxStatisticalTime.put(MAX_MONTH, monthValue > maxStatisticalTime.getOrDefault(MAX_MONTH, 1) ? monthValue : maxStatisticalTime.get(MAX_MONTH));
            });

            //得出最大的月份
            Integer maxMonth = maxStatisticalTime.get(MAX_MONTH);
            log.debug("\n==>打印最大月份==>{}", maxMonth);


            for (int i = 1; i <= maxMonth; i++) {
                xStrList.add(i + "月");
                Integer data = memberSurviveMonthMap.getOrDefault(i, 0);
                Integer data2 = memberLogOutMonthMap.getOrDefault(i, 0);
                yDataList.add(data);
                yDataList2.add(data2);
            }
            //设置属性
            cardCountVo.setTitle("月新增与流失客户数量统计")
                    .setXname("月")
                    .setTime(xStrList)
                    .setData(yDataList)
                    .setData2(yDataList2);
            log.debug("\n==>新增与流失人数统计页面按月度统计图表的x轴数据==>{}\n==>新增与流失人数统计页面按月份统计图表的Y轴数据==>{}\n==>打印新增与流失人数统计页面按月份统计图表的Y轴数据2==>{}", xStrList, yDataList, yDataList2);
        } else if (unit == 2) {
            //所有还存活的会员
            List<MemberEntity> memberSurviveForQuarterList = memberService.list(new QueryWrapper<MemberEntity>().select("create_time").likeRight("create_time", yearOfSelect).orderByDesc("create_time"));
            log.debug("\n==>打印指定年份里所有还存活的会员信息==>{}", memberSurviveForQuarterList);

            //所有注销的员工   //注意逻辑删除后的数据mp查不出来
            List<MemberEntity> memberLogOutForQuarterList = memberService.getMemberLogOutInSpecifyYear(yearOfSelect);
            log.debug("\n==>打印指定年份里所有注销的会员信息==>{}", memberLogOutForQuarterList);


            //创建一个盛放季度和当前季度新增会员数量的map
            HashMap<Integer, Integer> memberSurviveQuarterMap = new HashMap<>();
            //创建一个盛放季度和当前季度注销会员数量的map
            HashMap<Integer, Integer> memberLogOutQuarterMap = new HashMap<>();

            //获取存活的会员在选中年度内的最新入会时间的月份
            memberSurviveForQuarterList.forEach(member -> {
                //获取季度
                int quarter = (member.getCreateTime().getMonthValue() - 1) / 3 + 1;
                memberSurviveQuarterMap.put(quarter, memberSurviveQuarterMap.getOrDefault(quarter, 0) + 1);
                //将最大的季度放进map
                maxStatisticalTime.put(MAX_QUARTER, quarter > maxStatisticalTime.getOrDefault(MAX_QUARTER, 1) ? quarter : maxStatisticalTime.get(MAX_QUARTER));

            });

            //获取注销的会员在选中年度内的最新退会时间的月份
            memberLogOutForQuarterList.forEach(member -> {
                int quarter = (member.getLastModifyTime().getMonthValue() - 1) / 3 + 1;
                memberLogOutQuarterMap.put(quarter, memberLogOutQuarterMap.getOrDefault(quarter, 0) - 1);
                //将最大的季度放进map
                maxStatisticalTime.put(MAX_QUARTER, quarter > maxStatisticalTime.getOrDefault(MAX_QUARTER, 1) ? quarter : maxStatisticalTime.get(MAX_QUARTER));
            });

            //获取最大季度
            Integer maxQuarter = maxStatisticalTime.get(MAX_QUARTER);
            log.debug("\n==>打印最大季度==>{}", maxQuarter);

            for (int i = 1; i <= maxQuarter; i++) {
                xStrList.add(i + "季度");
                Integer data = memberSurviveQuarterMap.getOrDefault(i, 0);
                Integer data2 = memberLogOutQuarterMap.getOrDefault(i, 0);
                yDataList.add(data);
                yDataList2.add(data2);
            }
            //设置属性
            cardCountVo.setTitle("季度新增与流失客户数量统计")
                    .setXname("季度")
                    .setTime(xStrList)
                    .setData(yDataList)
                    .setData2(yDataList2);
            log.debug("\n==>新增与流失人数统计页面按季度统计图表的x轴数据==>{}\n==>打印新增与流失人数统计页面按季度统计图表的Y轴数据==>{}\n==>打印新增与流失人数统计页面按季度统计图表的Y轴数据2==>{}", xStrList, yDataList, yDataList2);
        } else {

            //当统计时间冲突时
            if (endYear < beginYear) {
                return R.error("起始时间与结束时间冲突！请修改后再查看统计结果");
            }

            List<MemberEntity> memberSurviveForYearList = memberService.list(new QueryWrapper<MemberEntity>().select("create_time")
                    .ge("create_time", LocalDateTime.of(beginYear, 1, 1, 0, 0, 0))
                    .le("create_time", LocalDateTime.of(endYear, 12, 31, 23, 59, 59))
                    .orderByDesc("create_time"));
            log.debug("\n==>打印在beginYear和endYear之间的所有存活的会员信息==>{}", memberSurviveForYearList);

            List<MemberEntity> memberLogOutForYearList = memberService.getMemberLogOutFromBeginYearToEndYear().stream().filter(member ->
                    member.getLastModifyTime().isAfter(LocalDateTime.of(beginYear, 1, 1, 0, 0))
                            &&
                            member.getLastModifyTime().isBefore(LocalDateTime.of(endYear, 12, 31, 23, 59)))
                    .collect(Collectors.toList());
            log.debug("\n==>打印在beginYear和endYear之间的所有所有已经注销的会员信息==>{}", memberLogOutForYearList);

            //创建一个盛放年度和当前年度新增会员数量的map
            HashMap<Integer, Integer> memberSurviveYearMap = new HashMap<>();
            //创建一个盛放年度和当前年度注销会员数量的map
            HashMap<Integer, Integer> memberLogOutYearMap = new HashMap<>();

            //遍历beginYear和endYear之间存活的会员信息
            memberSurviveForYearList.forEach(member -> {
                int year = member.getCreateTime().getYear();
                memberSurviveYearMap.put(year, memberSurviveYearMap.getOrDefault(year, 0) + 1);
            });

            //遍历beginYear和endYear之间注销的会员信息
            memberLogOutForYearList.forEach(member -> {
                int year = member.getLastModifyTime().getYear();
                memberLogOutYearMap.put(year, memberLogOutYearMap.getOrDefault(year, 0) - 1);
            });

            log.debug("\n==>年度和当前年度新增会员数量的map==>{}\n==>年度和当前年度注销会员数量的map==>{}", memberSurviveYearMap, memberLogOutYearMap);

            for (int i = beginYear; i <= endYear; i++) {
                xStrList.add(i + "年");
                Integer data = memberSurviveYearMap.getOrDefault(i, 0);
                Integer data2 = memberLogOutYearMap.getOrDefault(i, 0);
                yDataList.add(data);
                yDataList2.add(data2);
            }
            //设置属性
            cardCountVo.setTitle("年度新增与流失客户数量统计")
                    .setXname("年度")
                    .setTime(xStrList)
                    .setData(yDataList)
                    .setData2(yDataList2);
            log.debug("\n==>新增与流失人数统计页面按年度统计图表的x轴数据==>{}\n==>打印新增与流失人数统计页面按年度统计图表的Y轴数据==>{}\n==>打印新增与流失人数统计页面按年度统计图表的Y轴数据2==>{}", xStrList, yDataList, yDataList2);


        }


        return R.ok().put("data", cardCountVo);
    }

}
