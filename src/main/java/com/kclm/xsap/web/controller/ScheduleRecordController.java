package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kclm.xsap.config.CustomConfig;
import com.kclm.xsap.consts.KeyNameOfCache;
import com.kclm.xsap.consts.OperateType;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.ExpiryMap;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.*;
import com.kclm.xsap.web.cache.MapCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 中间表：排课计划表
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */

@Slf4j
@Controller
@RequestMapping("/schedule")
public class ScheduleRecordController {

    //外键约束异常
    private static final String SQLSTATE_23000 = "23000";


    @Resource
    private ScheduleRecordService scheduleRecordService;

    @Resource
    private CourseService courseService;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private CourseCardService courseCardService;

    @Resource
    private MemberCardService memberCardService;

    @Resource
    private ReservationRecordService reservationRecordService;

    @Resource
    private ClassRecordService classRecordService;

    @Resource
    private ConsumeRecordService consumeRecordService;

    @Resource
    private MemberBindRecordService memberBindRecordService;

    @Resource
    private MemberLogService memberLogService;

    @Resource
    private MapCacheService mapCacheService;

    @Resource
    private CustomConfig customConfig;


    /**
     * 跳转到排课课程表
     *
     * @return x_course_schedule.html
     */
    @GetMapping("/x_course_schedule.do")
    public String courseSchedule() {
        log.debug("\n==>跳转到排课课程表==>");
        return "course/x_course_schedule";
    }


    /**
     * 根据id跳转详细排课信息
     *
     * @param id    需要查看详细信息的id
     * @param model 保存传递"id"
     * @return x_course_schedule_detail.html
     */
    @GetMapping("/x_course_schedule_detail.do")
    public String courseScheduleDetail(@RequestParam("id") Long id, Model model) {
        log.debug("\n==>前端传入的要查看详细排课记录的id==>{}", id);
        model.addAttribute("ID", id);

        return "course/x_course_schedule_detail";
    }

    /**
     * 查询所有排课记录信息并封装成CourseScheduleVo【前端需要的json格式用于日程显示】
     * 优化：在stream中做查询显然极不合理，考虑在bean中封装需要查询的属性，做冗余保存
     *
     * @return vo数据
     */
    @PostMapping("/scheduleList.do")
    @ResponseBody
    public List<CourseScheduleVo> scheduleList() {
        //todo 要不要做个map缓存？

        //本地缓存
        ExpiryMap<KeyNameOfCache, Object> CACHE_SCHEDULE_LIST_INFO_MAP = mapCacheService.getCacheInfo();

        Object cacheValueOfMapForSchedule = CACHE_SCHEDULE_LIST_INFO_MAP.get(KeyNameOfCache.CACHE_SCHEDULE_INFO);
        //todo
        List<CourseScheduleVo> cacheOfSchedule = null;
        if (cacheValueOfMapForSchedule instanceof List) {
            log.debug("类型匹配，强制转化");
            cacheOfSchedule = (List<CourseScheduleVo>) cacheValueOfMapForSchedule;
        }
        if (null != cacheOfSchedule) {
            log.debug("直接返回了缓存中的数据");
            return  cacheOfSchedule;
        }

        List<ScheduleRecordEntity> scheduleRecordEntityList = scheduleRecordService.list();
        log.debug("\n==>查出的所有的排课记录==>{}", scheduleRecordEntityList);

        List<CourseScheduleVo> courseScheduleVoList = scheduleRecordEntityList.stream().map(entity -> {
            CourseEntity courseById = courseService.getById(entity.getCourseId());
            log.debug("\n==>stream中由当前课程记录对应的课程的详细数据==>{}", courseById);

            LocalDateTime startTime = LocalDateTime.of(entity.getStartDate(), entity.getClassTime());

            return new CourseScheduleVo()
                    .setTitle(courseById.getName() + "【" + employeeService.getById(entity.getTeacherId()).getName() + "】")
                    .setStart(startTime)
                    .setEnd(startTime.plusMinutes(courseById.getDuration()))
                    .setColor(courseById.getColor())
                    .setUrl("x_course_schedule_detail.do?id=" + entity.getId());
        }).collect(Collectors.toList());

        log.debug("\n==>后台根据排课记录封装的用于前端日程展示的list：courseScheduleVoList==>{}", courseScheduleVoList);

        //获取配置文件中设置的缓存时间
        Long cache_time = customConfig.getCache_time();

        //添加本地缓存
        CACHE_SCHEDULE_LIST_INFO_MAP.put(KeyNameOfCache.CACHE_SCHEDULE_INFO, courseScheduleVoList, cache_time);
        //return R.ok().put("data", courseScheduleVoList);
        return courseScheduleVoList;
    }



    /**
     * 保存前端提交的新增排课信息
     * 注意时间冲突
     *
     * @param entity 前端提交表单的封装
     * @return R->提交是否成功
     */
    @PostMapping("/scheduleAdd.do")
    @ResponseBody
    public R scheduleAdd(@Valid ScheduleRecordEntity entity,BindingResult bindingResult) {
        log.debug("\n==>前端表单提交的排课信息entity==>{}", entity);

        if (bindingResult.hasErrors()) {
            HashMap<String, String> map = new HashMap<>();
            bindingResult.getFieldErrors().forEach(item -> {
                String message = item.getDefaultMessage();
                String field = item.getField();
                map.put(field, message);
            });
            log.debug("\n==>errorMap==>{}", map);
            return R.error(400, "非法参数").put("errorMap", map);
        }

        if (null != entity.getCourseId() && null != entity.getTeacherId() && null != entity.getStartDate()) {
            entity.setCreateTime(LocalDateTime.now());

            //判断冲突
            //获取前端提交的课程开始日期
            LocalDate startDate = entity.getStartDate();
            //获取前台提交的课程的开始时间
            LocalTime startTime = entity.getClassTime();
            //获取课程时长
            Long courseDuration = entity.getCourseDuration();
            //计算课程结束时间
            LocalTime endTime = startTime.plusMinutes(courseDuration);
            //只要新增的课程的开始和结束时间不在其他课程的开始结束时间之内就可以无冲添加
            log.debug("\n==>提交的课程的开始时间==>{}\n提交的课程的结束时间==>{}", startTime, endTime);

            List<ScheduleRecordEntity> sameDateScheduleList = scheduleRecordService.getSameDateSchedule(startDate);
            if (!sameDateScheduleList.isEmpty()) {
                log.debug("\n==>打印当天的所有课程【此日志用于排查排课时间冲突】==>{}", sameDateScheduleList);

                //获取配置文件中设置的添加排课的间隙时间
                Long gap_minute = customConfig.getGap_minute();

                //查询新增课程开始时间
                for (ScheduleRecordEntity sameDateSchedule : sameDateScheduleList) {
                    LocalTime sameDateClassStartTime = sameDateSchedule.getClassTime();
                    LocalTime sameDateClassEndTime = sameDateClassStartTime.plusMinutes(sameDateSchedule.getCourseEntity().getDuration());
                    //加上10分钟：表示可以在下课之后至少经过gap_minute才能继续添加课程
                    if ((startTime.isAfter(sameDateClassStartTime) && startTime.isBefore(sameDateClassEndTime.plusMinutes(gap_minute))) || (endTime.isAfter(sameDateClassStartTime) && endTime.isBefore(sameDateClassEndTime.plusMinutes(gap_minute)))) {
                        log.debug("\n==>添加课程->添加时间冲突【我们默认设置下课十分钟后可以继续添加新的课程】");
                        return R.error("排课时间冲突");
                    }
                }
            }

            boolean isSaveSchedule = scheduleRecordService.save(entity);
            log.debug("\n==>保存排课信息是否成功isSaveSchedule:==>{}", isSaveSchedule);

            //添加排课信息之前删除缓存中的排课信息
            mapCacheService.getCacheInfo().remove(KeyNameOfCache.CACHE_SCHEDULE_INFO);
            log.debug("添加排课信息之前删除缓存中的排课信息");

            return R.ok("排课信息提交成功");
        } else {
            return R.error("请正确输入排课信息");
        }
    }


    /**
     * 排课记录复制
     *
     * @param sourceDateStr 要复制的源日期
     * @param targetDateStr 要复制的目的日期
     * @return R->复制结果
     */
    @PostMapping("/scheduleCopy.do")
    @ResponseBody
    public R scheduleCopy(@RequestParam("sourceDateStr") String sourceDateStr,
                          @RequestParam("targetDateStr") String targetDateStr) {
        log.debug("\n==>前端传入的要复制的源日期==>{}\n==>前端传入的要复制目的日期==>{}", sourceDateStr, targetDateStr);

        //查出源日期在数据库中的数据
        List<ScheduleRecordEntity> sourceRecordList = scheduleRecordService.list(new QueryWrapper<ScheduleRecordEntity>().eq("start_date", sourceDateStr));
        //查出目的日期在数据库中的数据
        List<ScheduleRecordEntity> targetRecordList = scheduleRecordService.list(new QueryWrapper<ScheduleRecordEntity>().eq("start_date", targetDateStr));
        //打印
        log.debug("\n==>源日期在数据库中的排课数据SourceRecordList==>{}\n==>目的日期在数据库中的排课数据TargetRecordList{}", sourceRecordList, targetRecordList);

        if (sourceRecordList.isEmpty()) {
            return R.error("源日期中没有课程记录");
        }

        //这里的逻辑可以定义
        if (!targetRecordList.isEmpty()) {
            return R.error("目的日期中已经存在排课记录");
        }

        List<ScheduleRecordEntity> newTargetRecordList = sourceRecordList.stream().map(entity -> {
                    ScheduleRecordEntity recordEntity = new ScheduleRecordEntity();
                    recordEntity.setCourseId(entity
                            .getCourseId())
                            .setTeacherId(entity.getTeacherId())
                            .setStartDate(LocalDate.parse(targetDateStr))
                            .setClassTime(entity.getClassTime())
                            .setLimitSex(entity.getLimitSex())
                            .setLimitAge(entity.getLimitAge())
                            .setCreateTime(LocalDateTime.now());
                    return recordEntity;
                }
        ).collect(Collectors.toList());
        scheduleRecordService.saveBatch(newTargetRecordList);

        //添加排课信息之前删除缓存中的排课信息
        mapCacheService.getCacheInfo().remove(KeyNameOfCache.CACHE_SCHEDULE_INFO);
        log.debug("添加排课信息之前删除缓存中的排课信息");

        return R.ok("复制成功！");

    }


    /**
     * 根据id查出的排课具体信息【页面的头部信息】，使用vo封装
     *
     * @param id 具体的排课记录id，前端传入
     * @return R -> 具体id的排课数据
     */
    @PostMapping("/scheduleDetail.do")
    @ResponseBody
    public R scheduleDetail(@RequestParam("id") Long id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");
        //由传入id查询排课信息
        ScheduleRecordEntity scheduleById = scheduleRecordService.getById(id);
        log.debug("\n==>要查询的排课记录的id是,==>{}\n根据id查询到的排课信息：scheduleRecordEntityById==>{}", id, scheduleById);

        //由查出的排课信息继续查出对应课程id，最后查出课程信息
        Long courseId = scheduleById.getCourseId();
        CourseEntity courseById = courseService.getById(courseId);
        log.debug("\n==>根据课程id查到的课程信息:courseById==>{}", courseById);

        //没事看看jdk8日期类
        LocalDateTime startTimeFormat = LocalDateTime.of(scheduleById.getStartDate(), scheduleById.getClassTime());
        String startTime = formatter.format(startTimeFormat);
        Long duration = courseById.getDuration();
        String endTime = formatter.format(startTimeFormat.plusMinutes(duration));

        //根据关联表查询所有支持该课程的关联信息
        List<CourseCardEntity> courseCardEntityList = courseCardService.list(new QueryWrapper<CourseCardEntity>().eq("course_id", courseId));
        log.debug("\n==>根据会员卡和课程关联信息查出全部支持该课程的会员卡信息courseCardEntityList：==>{}", courseCardEntityList);
        List<Long> cardIds = courseCardEntityList.stream().map(CourseCardEntity::getCardId).collect(Collectors.toList());
        log.debug("\n==>由上面courseCardEntityList流式取出会员卡id集合==>{}", cardIds);
        //流式取出会员卡名字并格式化输出
        List<String> supportCards = memberCardService.listByIds(cardIds).stream().map(card ->
                "【" + card.getName() + "】"
        ).collect(Collectors.toList());
        log.debug("\n==>支持的会员卡列表==>{}", supportCards);

        //查出上课老师名字
        String teacherName = employeeService.getById(scheduleById.getTeacherId()).getName();

        //封装
        ScheduleDetailsVo details = new ScheduleDetailsVo()
                .setCourseName(courseById.getName())
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setDuration(duration)
                .setLimitSex(scheduleById.getLimitSex() == null ? "无限制" : scheduleById.getLimitSex())
                .setLimitAge(scheduleById.getLimitAge() == null ? -1 : scheduleById.getLimitAge())
                .setSupportCards(supportCards)
                .setTeacherName(teacherName)
                .setOrderNums(scheduleById.getOrderNums())
                .setClassNumbers(courseById.getContains());
        log.debug("\n==>封装后的details：==>{}", details);

        return new R().put("data", details);
    }


    /**
     * 异步请求返回课程详情页已预约该课程信息【有问题，修改】
     *
     * @param id 前台传入的课程id
     * @return r-> 已预约信息
     */
    @PostMapping("/reservedList.do")
    @ResponseBody
    public R reservedList(@RequestParam("id") Long id) {
        //这个FLAG用于区分是查询全部记录还是只查询预约有效的记录;因为用的是同一个sql
        final String FLAG = "RESERVED";
        List<ScheduleDetailReservedVo> reservedVos = reservationRecordService.getReserveList(id, FLAG);
        log.debug("\n==>后台封装的已预约记录==>{}", reservedVos);

        List<ScheduleDetailReservedVo> collect = reservedVos.stream().peek(vo -> {
            if (vo.getOperateTime() == null) {
                vo.setOperateTime(vo.getCreateTime());
            }
        })/*.filter(vo -> {
            LocalDateTime classDateTime = LocalDateTime.of(vo.getStartDate(), vo.getClassTime());
            log.debug("\n==>上课时间是：：==>{}", classDateTime);
            boolean before = classDateTime.isAfter(LocalDateTime.now());
            log.debug("\n==>before==>{}", before);
            return before;
        })*/.collect(Collectors.toList());

        return new R().put("data", collect);
    }


    /**
     * 异步请求获取全部预约记录     【注意代码复用和上面的请求】
     *
     * @param id 前台传入的排课记录id
     * @return r ->全部预约记录数据
     */
    @PostMapping("/reserveRecord.do")
    @ResponseBody
    public R reserveRecord(@RequestParam("id") Long id) {
        log.debug("\n==>传入的排课记录id==>{}", id);
        List<ScheduleDetailReservedVo> reservedVos = reservationRecordService.getReserveList(id, null);
        log.debug("\n==>后台封装的全部预约记录==>{}", reservedVos);
        List<ScheduleDetailReservedVo> collect = reservedVos.stream().peek(vo -> {
            if (vo.getOperateTime() == null) {
                vo.setOperateTime(vo.getCreateTime());
            }
        }).collect(Collectors.toList());

        return new R().put("data", collect);
    }


    /**
     * 异步请求获取上课数据
     *
     * @param id 前台传入的排课记录id
     * @return r->上课数据
     */
    @PostMapping("/classRecord.do")
    @ResponseBody
    public R classRecord(@RequestParam("id") Long id) {
        log.debug("\n==>传入的排课记录id==>{}", id);
        List<ClassRecordVo> classRecordVos = classRecordService.getClassRecordList(id);
        log.debug("\n==>后台封装的上课数据信息==>{}", classRecordVos);

        //给操作时间赋值并过滤留下已经开始上课的预约用户信息和上课信息
        List<ClassRecordVo> collect = classRecordVos.stream().map(vo -> {
            ClassRecordVo recordVo = new ClassRecordVo();
            BeanUtils.copyProperties(vo, recordVo);
//            Long cardBindId = memberBindRecordService.getOne(new QueryWrapper<MemberBindRecordEntity>().eq("member_id", vo.getMemberId()).eq("card_id", vo.getCardId())).getId();
//            Long cardBindId = vo.getCardId();
//            log.debug("\n==>在上课信息vo中填充会员卡绑定实体id==>{}", cardBindId);
//            recordVo.setCardId(cardBindId);
            recordVo.setOperateTime(null == vo.getOperateTime() ? vo.getCreateTimes() : vo.getOperateTime());
            return recordVo;
        }).filter(vo -> {
            LocalDateTime classDateTime = LocalDateTime.of(vo.getStartDate(), vo.getClassTime());
            return classDateTime.isBefore(LocalDateTime.now());
//            return classDateTime.isAfter(LocalDateTime.now());
        }).collect(Collectors.toList());
        log.debug("\n==>过滤后留下的已经开始上课的上课数据==>{}", collect);

        return new R().put("data", collect);
    }


    /**
     * 根据id删除排课记录
     *
     * @param id 前台传入的要删除的scheduleId
     * @return r -> 删除结果
     */
    @PostMapping("/deleteOne.do")
    @ResponseBody
    public R deleteOne(@RequestParam("id") Long id) {

        boolean isRemoveSchedule = false;
        try {
            isRemoveSchedule = scheduleRecordService.removeById(id);

        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLIntegrityConstraintViolationException) {
                String sqlState = ((SQLIntegrityConstraintViolationException) cause).getSQLState();
                if (SQLSTATE_23000.equals(sqlState)) {
                    return R.error("删除失败!此课程已经生成了预约、上课、排课等记录，不可被删除！");
                }
            } else {
                e.printStackTrace();
            }
        }
        if (isRemoveSchedule) {

            //添加排课信息之前删除缓存中的排课信息
            mapCacheService.getCacheInfo().remove(KeyNameOfCache.CACHE_SCHEDULE_INFO);
            log.debug("添加排课信息之前删除缓存中的排课信息");

            return R.ok("删除成功！即将跳转..");
        } else {
            return R.error("删除失败！");
        }
    }


    /**
     * 一键确认扣费
     *
     * @param scheduleId 排课记录id
     * @param operator   操作人
     * @return r -> 一键确认成功结果
     */
    @PostMapping("/consumeEnsureAll.do")
    @ResponseBody
    @Transactional
    public R consumeEnsureAll(@RequestParam("scheduleId") Long scheduleId,
                              @RequestParam("operator") String operator) {
        log.debug("\n==>前台传入的排课记录id==>{}\n==>前台传入的操作人信息==>{}", scheduleId, operator);
        //查预约了该课程的所有预约记录
        List<ReservationRecordEntity> reservationListByScheduleId = reservationRecordService.list(new QueryWrapper<ReservationRecordEntity>().eq("schedule_id", scheduleId).eq("status", 1));
        log.debug("\n==>打印所有预约了这节课的预约信息==>{}", reservationListByScheduleId);
        //判空，如果没有有效预约，说明没有存在的上课记录
        if (reservationListByScheduleId.isEmpty()) {
            return R.error("还没有人预约这节课哦！");
        }

        //当所有会员都已经确认上课时，一键确认无效
        List<Integer> checkStatusList = classRecordService.list(new QueryWrapper<ClassRecordEntity>().select("check_status").eq("schedule_id", scheduleId)).stream().map(ClassRecordEntity::getCheckStatus).distinct().collect(Collectors.toList());
        if (checkStatusList.size() == 1 && checkStatusList.get(0) == 1) {
            log.debug("\n==>没有需要确认的会员");
            return R.error("所有会员均已确认");
        }

        //这节课的排课记录信息
        ScheduleRecordEntity scheduleById = scheduleRecordService.getById(scheduleId);


        /*
        超过上课时间再显示所有用户
        根据传入的排课记录id查询所有预约了该课的会员，处理会员卡表，上课记录表，消费记录表，会员操作日志表，（预约记录表）
         */
        //取出上课时间判断是否可以扣费,没到上课时间不应该可以扣费
        LocalDateTime classStartDateTime = LocalDateTime.of(scheduleById.getStartDate(), scheduleById.getClassTime());
        log.debug("\n==>打印该节课的上课时间：==>{}", classStartDateTime);
        if(classStartDateTime.isAfter(LocalDateTime.now())) {
            return R.error("上课时间未到！请稍后再试");
        }
        Long courseId = scheduleById.getCourseId();
        CourseEntity courseById = courseService.getById(courseId);
        //得出单节课消耗的次数
        Integer timesCost = courseById.getTimesCost();
        log.debug("\n==>打印这节课的消耗次数==>{}", timesCost);

        //删除会员卡统计缓存
        mapCacheService.getCacheInfo().remove(KeyNameOfCache.CACHE_OF_MEMBER_CARD_INFO);
        log.debug("\n==>一键扣费后删除map中的会员卡信息缓存");


        for (ReservationRecordEntity entity : reservationListByScheduleId) {

            //获取当前会员id
            Long memberId = entity.getMemberId();
            //获取会员卡实体的id
            Long bindId = entity.getCardId();
            MemberBindRecordEntity currentCardBindRecord = memberBindRecordService.getById(bindId);

            //获取此卡单次预约的人数（用于和一节课消耗次数相乘求此卡减去的次数）
            Integer reserveNums = entity.getReserveNums();

            //获取当前预约记录的所有消耗次数
            int allTimeCostForCurrentReserve = reserveNums * timesCost;

            //获取当前会员卡的应扣金额【余额/剩余次数*该卡消耗所有次数】
            BigDecimal amountsPayable = BigDecimal.valueOf(currentCardBindRecord.getReceivedMoney().doubleValue() / currentCardBindRecord.getValidCount() * allTimeCostForCurrentReserve);



            //1.添加操作记录
            MemberLogEntity logEntity = new MemberLogEntity()
                    .setType(OperateType.CLASS_DEDUCTION_OPERATION.getMsg())
                    .setMemberBindId(bindId)
                    .setOperator(operator)
                    .setCreateTime(LocalDateTime.now())
                    .setInvolveMoney(amountsPayable)
                    .setCardCountChange(allTimeCostForCurrentReserve);
            boolean isSaveLog = memberLogService.save(logEntity);
            log.debug("\n==>添加操作记录是否成功==>{}", isSaveLog);
            if (!isSaveLog) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return R.error("一键确认扣费失败");
            }

            //2.添加消费记录
            ConsumeRecordEntity consume = new ConsumeRecordEntity()
                    .setOperateType(OperateType.CLASS_DEDUCTION_OPERATION.getMsg())
                    .setMoneyCost(amountsPayable)
                    .setCardCountChange(allTimeCostForCurrentReserve)
                    .setOperator(operator)
                    .setMemberBindId(bindId)
                    .setCreateTime(LocalDateTime.now())
                    .setLogId(logEntity.getId());
            boolean isSaveConsumeRecord = consumeRecordService.save(consume);
            log.debug("\n==>添加消费记录是否成功==>{}", isSaveConsumeRecord);
            if (!isSaveConsumeRecord) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return R.error("一键确认扣费失败！");
            }


            //3.更新会员卡信息
            currentCardBindRecord.setValidCount(currentCardBindRecord.getValidCount() - allTimeCostForCurrentReserve)
                    .setReceivedMoney(currentCardBindRecord.getReceivedMoney().subtract(amountsPayable))
                    .setLastModifyTime(LocalDateTime.now())
                    .setVersion(currentCardBindRecord.getVersion() + 1);
            boolean isUpdateBind = memberBindRecordService.updateById(currentCardBindRecord);
            log.debug("\n==>更新会员卡信息是否成功==>{}", isUpdateBind);
            if (!isUpdateBind) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return R.error("一键确认扣费失败！");
            }

            //4.更新上课记录信息
            ClassRecordEntity one = classRecordService.getOne(new QueryWrapper<ClassRecordEntity>().eq("member_id", memberId).eq("schedule_id", scheduleId));
            if (one.getCheckStatus() == 0) {
                one.setCheckStatus(1).setLastModifyTime(LocalDateTime.now()).setVersion(one.getVersion() + 1);
                boolean isUpdateClassRecord = classRecordService.updateById(one);
                log.debug("\n==>更新上课记录是否成功==>{}", isUpdateClassRecord);
                if (!isUpdateClassRecord) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return R.error("一键确认扣费失败！");
                }
            }
        }

        return R.ok("扣费成功！");


    }


    /**
     * 根据前台传入的会员卡id计算此卡 amountPayablePerPerson
     * @param bindCardId 传入的会员实体卡的id
     * @return 单次应付金额
     */
    @PostMapping("/queryAmountsPayable.do")
    @ResponseBody
    public R queryAmountsPayable(Long bindCardId) {
        log.debug("\n==>打印传入的绑定卡id==>{}", bindCardId);
        MemberBindRecordEntity bindRecordEntity = memberBindRecordService.getOne(new QueryWrapper<MemberBindRecordEntity>().select("received_money", "valid_count").eq("id", bindCardId));
        if (null != bindRecordEntity) {
            if (bindRecordEntity.getReceivedMoney().compareTo(BigDecimal.ZERO) <= 0 || bindRecordEntity.getValidCount() <= 0) {
                return R.error("卡余额或剩余次数不足,请检查卡余量或充值后再扣费");
            }
            BigDecimal amountPayablePerPerson = BigDecimal.valueOf(bindRecordEntity.getReceivedMoney().doubleValue() / bindRecordEntity.getValidCount());
            DecimalFormat format = new DecimalFormat(".00");
            Float amountPayablePerPersonFloat = Float.valueOf(format.format(amountPayablePerPerson));
//            float amountPayablePerPerson = bindRecordEntity.getReceivedMoney().floatValue() / bindRecordEntity.getValidCount();
            log.debug("\n==>打印课程详情页中单独确认扣费表单的应扣次数==>{}", amountPayablePerPersonFloat);
            return R.ok().put("data", amountPayablePerPersonFloat);
        } else {
            return R.error("无此卡");
        }

    }


    /**
     * 上课信息详情页单次确认上课
     * @param consumeFormVo 前台传入的表单信息的封装
     * @return r -> 确认结果
     */
    @PostMapping("/consumeEnsure.do")
    @ResponseBody
    @Transactional
    public R consumeEnsure(@Valid ConsumeFormVo consumeFormVo, BindingResult bindingResult) {
        log.debug("\n==>上课扣费弹出框的前台传入的消费信息consumeInfoVo==>{}", consumeFormVo);

        //如果没有设置扣除天数
        consumeFormVo.setCardDayChange(consumeFormVo.getCardDayChange() == null ? 0 : consumeFormVo.getCardDayChange());

        if(bindingResult.hasErrors()) {
            if (bindingResult.hasErrors()) {
                FieldError fieldError = bindingResult.getFieldErrors().get(0);
                HashMap<String, String> errorMap = new HashMap<>();
                errorMap.put("cardCountChange", fieldError.getDefaultMessage());
                return R.error(400, "非法参数").put("errorMap", errorMap);
            }
        }

        //获取当前用户所使用的会员卡信息
        MemberBindRecordEntity bindRecordById = memberBindRecordService.getById(consumeFormVo.getCardBindId());

        //获取应付金额
        //BigDecimal amountsPayable = BigDecimal.valueOf(bindRecordById.getReceivedMoney().doubleValue() / bindRecordById.getValidCount());


        //1.添加操作记录
        MemberLogEntity memberLogEntity = new MemberLogEntity()
                .setType(OperateType.CLASS_DEDUCTION_OPERATION.getMsg())
                .setOperator(consumeFormVo.getOperator())
                .setMemberBindId(consumeFormVo.getCardBindId())
                .setCreateTime(LocalDateTime.now())
                .setCardCountChange(consumeFormVo.getCardCountChange())
                .setInvolveMoney(consumeFormVo.getAmountOfConsumption())
                .setCardDayChange(consumeFormVo.getCardDayChange())
                .setNote(consumeFormVo.getNote());
        boolean isSaveLog = memberLogService.save(memberLogEntity);

        log.debug("\n==>添加操作记录是否成功==>{}", isSaveLog);
        if (!isSaveLog) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.debug("单次确认上课中的添加操作记录失败");
            return R.error("确认上课失败！");
        }

        //2.添加消费记录
        ConsumeRecordEntity consumeRecordEntity = new ConsumeRecordEntity()
                .setOperateType(OperateType.CLASS_DEDUCTION_OPERATION.getMsg())
                .setCardCountChange(consumeFormVo.getCardCountChange())
                .setMoneyCost(consumeFormVo.getAmountOfConsumption())
                .setCardDayChange(consumeFormVo.getCardDayChange())
                .setOperator(consumeFormVo.getOperator())
                .setNote(consumeFormVo.getNote())
                .setMemberBindId(consumeFormVo.getCardBindId())
                .setCreateTime(LocalDateTime.now())
                .setLogId(memberLogEntity.getId())
                .setScheduleId(consumeFormVo.getScheduleId());
        boolean isSaveConsumeRecord = consumeRecordService.save(consumeRecordEntity);
        log.debug("\n==>添加消费记录是否成功==>{}", isSaveConsumeRecord);
        if(!isSaveConsumeRecord) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.debug("单次确认上课中的添加消费记录失败");
            return R.error("确认上课失败！");
        }

        //3.修改会员卡剩余次数信息

        bindRecordById.setValidCount(bindRecordById.getValidCount() - consumeFormVo.getCardCountChange())
                .setValidDay(bindRecordById.getValidDay() - consumeFormVo.getCardDayChange())
                .setReceivedMoney(bindRecordById.getReceivedMoney().subtract(consumeFormVo.getAmountOfConsumption()))
                .setLastModifyTime(LocalDateTime.now())
                .setVersion(bindRecordById.getVersion() + 1);
        boolean isUpdateBind = memberBindRecordService.updateById(bindRecordById);
        log.debug("\n==>更新会员卡实体信息是否成功==>{}", isUpdateBind);
        if (!isUpdateBind) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.debug("单次确认上课中添加操作记录失败！");
            return R.error("确认上课失败！");
        }
        //4.更新上课信息
        boolean isUpdateClassRecord = classRecordService.update(new UpdateWrapper<ClassRecordEntity>().eq("id", consumeFormVo.getClassId()).set("check_status", 1));
        log.debug("\n==>更新上课信息是否成功==>{}", isUpdateClassRecord);
        if (!isUpdateClassRecord) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.debug("单次确认上课中更新上课信息失败!");
            return R.error("确认上课失败");
        }

        //删除会员卡统计信息的缓存
        mapCacheService.getCacheInfo().remove(KeyNameOfCache.CACHE_OF_MEMBER_CARD_INFO);
        log.debug("\n==>确认扣费后删除map中的会员卡信息缓存");

        return R.ok("确认上课成功");
    }


    /**
     * 返回会员详情页扣费中课程suggest所需的数据
     * @return r -> for suggest
     */
    @GetMapping("/toSearch.do")
    @ResponseBody
    public R toSearch() {

        //获取当前日期
        LocalDate now = LocalDate.now();

        //查询最近三天的所有排课记录
        List<ScheduleRecordEntity> scheduleWith_id_courseId_teacherId_time = scheduleRecordService.list(new QueryWrapper<ScheduleRecordEntity>()
                .select("id", "course_id", "teacher_id", "start_date", "class_time").le("start_date", now).ge("start_date", now.minusDays(3)));

        //使用流获取前端的suggest需要的vo的list
        List<ScheduleForConsumeSearchVo> scheduleForSearchVos = scheduleWith_id_courseId_teacherId_time.stream().map(schedule -> {
            //获取课程名字
            String courseName = courseService.getById(schedule.getCourseId()).getName();
            //获取老师名字
            String teacherName = employeeService.getById(schedule.getTeacherId()).getName();
            //创建vo并复制
            return new ScheduleForConsumeSearchVo()
                    .setScheduleId(schedule.getId())
                    .setCourseName(courseName)
                    .setTeacherName(teacherName)
                    .setClassDateTime(LocalDateTime.of(schedule.getStartDate(), schedule.getClassTime()));
        }).collect(Collectors.toList());
        log.debug("\n==>会员扣费中课程的搜索建议：suggest所需的数据==>{}", scheduleForSearchVos);

        return R.ok().put("value", scheduleForSearchVos);

    }



    /**
     * 导出预约数据
     * todo :...
     * @return 导出
     */
    @PostMapping("/exportReserve.do")
    @ResponseBody
    public R exportReserve() {
        return null;
    }


}
