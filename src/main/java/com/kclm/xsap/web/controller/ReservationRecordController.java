package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


/**
 * 预约记录
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@Controller
@RequestMapping("/reserve")
@Slf4j
public class ReservationRecordController {
    @Autowired
    private ReservationRecordService reservationRecordService;

    @Autowired
    private GlobalReservationSetService globalReservationSetService;

    @Autowired
    private ScheduleRecordService scheduleRecordService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ClassRecordService classRecordService;

    @Autowired
    private MemberBindRecordService memberBindRecordService;


    /**
     * 预约数据批量导出
     *
     * @return 导出
     */
    @PostMapping("/exportReserveList.do")
    @ResponseBody
    public R exportReserveList() {

        return null;
    }


    /**
     * 取消预约,通过修改预约记录的status
     * 考虑全局预约设置--取消预约时间设置
     *
     * @param reserveId 要取消的预约记录的id
     * @return r -> entity(当前预约记录实体)
     */
    @PostMapping("/cancelReserve.do")
    @ResponseBody
    @Transactional
    public R cancelReserve(@RequestParam("reserveId") Long reserveId) {
        //获取全局预约设置
        GlobalReservationSetEntity globalReservationSet = globalReservationSetService.getById(1);

        //获取全局预约取消时间设置
        //获取模式
        Integer appointmentCancelMode = globalReservationSet.getAppointmentCancelMode();
        //获取预约取消的小时数 cancel_hour            appointment_cancel_mode 2
        Integer cancelHour = globalReservationSet.getCancelHour();
        log.debug("\n全局预约取消时间设置：==> 上课前{}小时可取消", cancelHour);

        //获取预约取消的天数和时间点
        Integer cancelDay = globalReservationSet.getCancelDay();
        LocalTime cancelTime = globalReservationSet.getCancelTime();
        log.debug("\n==>全局预约取消时间设置==>上课前{}天的{}可取消", cancelDay, cancelTime);

        //获取当前操作【取消操作】的时间
        LocalDateTime nowOperationTime = LocalDateTime.now();
        log.debug("\n==>当前操作的时间==>{}", nowOperationTime);
        //根据当前预约记录id查询完整信息
        ReservationRecordEntity entity = reservationRecordService.getById(reserveId).setCreateTime(nowOperationTime);
        log.debug("\n==>根据预约记录id查到的完整记录信息==>{}", entity);


        //根据预约记录信息查询课程的开始时间
        LocalDate classStartDate = scheduleRecordService.getById(entity.getScheduleId()).getStartDate();
        LocalTime classStartTime = scheduleRecordService.getById(entity.getScheduleId()).getClassTime();
        LocalDateTime classStartTimeOfSchedule = LocalDateTime.of(classStartDate, classStartTime);
        log.debug("\n==>预约的课程的开始时间是==>{}", classStartTimeOfSchedule);


        if (appointmentCancelMode == 1) {
            log.debug("\n已选模式1：不设时间限制【上课前均可取消】\n appointmentCancelMode ==>{}", appointmentCancelMode);
            if (nowOperationTime.isBefore(classStartTimeOfSchedule)) {
                return updateOfCancel(entity);
            } else {
                return R.error("已经超过开课时间,无法取消");
            }

        } else if (appointmentCancelMode == 2) {
            log.debug("\n 已选模式2==>appointmentCancelMode ==>{}\n：上课前{}小时可取消预约", appointmentCancelMode, cancelHour);
            //获取具体可取消的最迟时间
            LocalDateTime cancelDeadlineTime = classStartTimeOfSchedule.minusHours(cancelHour);
            if (nowOperationTime.isBefore(cancelDeadlineTime)) {
                return updateOfCancel(entity);
            } else {
                return R.error("已超过可取消的最迟时间");
            }

        } else {
            log.debug("\n 已选模式2==>appointmentCancelMode ==>{}\n：上课前xx天的{}:{}可取消预约", appointmentCancelMode, cancelHour, cancelTime);
            //获取具体可取消的最迟时间
            LocalDate cancelDeadlineDate = classStartDate.minusDays(cancelDay);
            LocalDateTime cancelDeadlineTime = LocalDateTime.of(cancelDeadlineDate, cancelTime);
            if (nowOperationTime.isBefore(cancelDeadlineTime)) {
                return updateOfCancel(entity);
            } else {
                return R.error("已超过可取消的最迟时间");
            }
        }
    }

    /**
     * 取消预约的数据库操作
     *
     * @param entity 要更新的记录实体
     */
    private R updateOfCancel(ReservationRecordEntity entity) {

        //判断该节课是否已经扣费成功【因为扣费操作是原子操作，这里通过判断用户是否确认上课为据】
        QueryWrapper<ClassRecordEntity> queryWrapper = new QueryWrapper<ClassRecordEntity>().eq("member_id", entity.getMemberId()).eq("card_name", entity.getCardName()).eq("schedule_id", entity.getScheduleId());

        ClassRecordEntity one = classRecordService.getOne(queryWrapper);
        if (one.getCheckStatus() == 1) {
            return R.error("课程已经扣费,无法取消预约");
        }

        //通过修改预约记录的状态达到取消预约
        entity.setCancelTimes(entity.getCancelTimes() + 1).setStatus(0).setVersion(entity.getVersion() + 1);
        boolean isUpdate = reservationRecordService.updateById(entity);
        log.debug("\n==>更新预约记录状态和取消次数是否成功==>{}", isUpdate);
        if (isUpdate) {

            boolean isRemoveClassRecord = classRecordService.remove(queryWrapper);
            log.debug("\n==>取消预约而删除上课记录是否成功==>{}", isRemoveClassRecord);

            //取出预约记录的排课记录id..
            Long scheduleId = entity.getScheduleId();
            //根据排课记录id查出排课记录完整信息
            ScheduleRecordEntity scheduleById = scheduleRecordService.getById(scheduleId);
            //查出该课程的容纳人数
            Integer contains = courseService.getById(scheduleById.getCourseId()).getContains();
            log.debug("\n==>该课程可容纳的人数==>{}", contains);
            //查出该课程已经预约了多少人
//            int reservedCount = reservationRecordService.count(new QueryWrapper<ReservationRecordEntity>().eq("schedule_id", scheduleId).eq("status", 1));
//            log.debug("\n==>该课程目前已经预约的人数==>{}", reservedCount);
            Integer currentOrderNums = scheduleById.getOrderNums();
            log.debug("\n==>该课程当前已经预约的人数==>{}", currentOrderNums);
            boolean isUpdateReserveNums = scheduleRecordService.update(new UpdateWrapper<ScheduleRecordEntity>().eq("id", scheduleId).set("order_nums", currentOrderNums - entity.getReserveNums()));
            if (isUpdateReserveNums) {
                log.debug("\n==>更新预约记录状态和取消次数成功\n新的预约记录==>{}", entity);
                return R.ok("预约取消成功！");
            } else {
                log.error("取消预约导致可容纳人数为负！？");
                return R.ok();
            }
        } else {
            log.debug("\n==>预约取消失败！更新失败！!");
            return R.error("预约取消失败");
        }
    }


    /**
     * 添加会员预约【面向前端url】
     *
     * @param entity 前端传入的预约表单信息封装
     * @return r -> 预约结果
     */
    @PostMapping("/addReserve.do")
    @ResponseBody
    @Transactional
    public R addReserve(ReservationRecordEntity entity) {
        /*添加预约
        首先查看该会员

        1.全局预约设置
        2.会员卡剩余次数能否支持
        3.该课程的容纳人数是否已满  todo

        预约开始时间：可以提前xx天预约              预约开始时间的模式，    appointment_start_mode：1：不限制会员可提前预约天数；2：限制天数

        预约截至时间： 预约截至上课前xx小时         预约截止时间模式    appointment_deadline_mode：   1：不限制截止时间；2：限制为上课前xx小时；3：限制为上课前xx天xx：xx（时间点）
                    预约截至上课前xx天xx时间点

        */
        log.debug("\n==>打印前端传入的预约信息==>{}", entity);
        if (entity.getMemberId() == null || entity.getCardName() == null) {
            return R.error("请填写关键信息");
        }


        //查看是否已经预约过
        ReservationRecordEntity existOne = reservationRecordService.getOne(new QueryWrapper<ReservationRecordEntity>()
                .eq("member_id", entity.getMemberId())
                .eq("card_name", entity.getCardName())
                .eq("schedule_id", entity.getScheduleId()));
        log.debug("\n==>根据表单提交信息查看是否已经预约过");
        if (null != existOne && null != existOne.getId()) {
            log.debug("\n==>该会员已经使用这张会员卡预约过这节课");
            //判断预约是否有效
            if (existOne.getStatus() == 1) {
                return R.error("您已预约过本节课程");
            }
            //此时表示预约过但已经取消,此时需要判断是否预约超过三次
            Integer cancelTimes = existOne.getCancelTimes();
            log.debug("\n==>已经取消预约的次数==>{}", cancelTimes);
            if (cancelTimes > 3) {
                //当预约取消超过三次即不可再预约
                //todo 记录其实不应该删除！考虑增加逻辑删除字段
                log.debug("\n==>当取消次数达到三次==>");
                return R.error("您已经取消预约超过三次,不可预约");
            } else {
                //未达到三次，采取更新//todo 预约取消的时候要更新取消次数
                log.debug("\n==>存在预约记录,且取消次数未超过三次==>即将更新记录");
                existOne.setLastModifyTime(LocalDateTime.now());
                return addOrUpdateReservation(existOne);
            }
        }

        //不存在预约记录,下面是添加记录
        log.debug("\n==>不存在预约记录==>首次添加预约记录需要对比限制信息");

        //查询该会员的信息(性别、年龄，用于对比限制条件)
        MemberEntity memberById = memberService.getById(entity.getMemberId());
        //查询课程信息
        ScheduleRecordEntity scheduleById = scheduleRecordService.getById(entity.getScheduleId());
        int age = (int) memberById.getBirthday().until(LocalDateTime.now(), ChronoUnit.YEARS);
        //当课程存在性别限制信息时判断会员信息是否满足
        if ((null != scheduleById.getLimitSex()) && (!scheduleById.getLimitSex().equals(memberById.getSex()))) {
            log.debug("性别不符");
            return R.error("性别不符和该课程");
        }
        //当课程存在年龄限制时，判断会员年龄是否满足
        if ((null != scheduleById.getLimitAge()) && (age < scheduleById.getLimitAge())) {
            log.debug("年龄不符");
            return R.error("年龄不符合该课程");
        }

        entity.setCreateTime(LocalDateTime.now());
        return addOrUpdateReservation(entity);
    }


    /**
     * 添加或更新预约记录逻辑
     *
     * @param entity 前端表单
     * @return r ->添加结果
     */
    private R addOrUpdateReservation(ReservationRecordEntity entity) {
        entity.setStatus(1);
        log.debug("\n==>传入的预约记录是==>{}", entity);

        //通过排课计划id获取该课程开始时间
        LocalDate classStartDate = scheduleRecordService.getById(entity.getScheduleId()).getStartDate();
        LocalTime classStartTime = scheduleRecordService.getById(entity.getScheduleId()).getClassTime();
        LocalDateTime classStartTimeOfSchedule = LocalDateTime.of(classStartDate, classStartTime);


        //获取当前操作的时间
        LocalDateTime nowOperationTime = entity.getLastModifyTime() == null ? entity.getCreateTime() : entity.getLastModifyTime();
        log.debug("\n==>当前操作的时间是==>{}", nowOperationTime);


        GlobalReservationSetEntity globalReservationSet = globalReservationSetService.getById(1);
        //获取预约开始所选用的模式
        Integer appointmentStartMode = globalReservationSet.getAppointmentStartMode();
        //获取预约截止时间所选用的模式
        Integer appointmentDeadlineMode = globalReservationSet.getAppointmentDeadlineMode();

        //获取可提前预约的天数【模式一】
        Integer startDay = globalReservationSet.getStartDay();
        log.debug("\n==>打印全局预约相关设置：可提前预约的天数==>{},-1表示未生效", startDay);

        //获取预约截至时间【上课前多少小时】【模式二】
        Integer endHour = globalReservationSet.getEndHour();
        log.debug("\n==>打印全局预约相关设置：预约截至时间：上课前xx小时==>{},-1表示未生效", endHour);

        //获取预约截至时间【上课前多少天的多少小时】【模式三】//todo 未生效时可以在设置全局设置时就将endTime置为null;
        Integer endDay = globalReservationSet.getEndDay();
        LocalTime endTime = globalReservationSet.getEndTime();
        log.debug("\n==>打印全局预约相关设置：预约截至时间：上课前xx天==>{} ==>xx小时{}, -1表示未生效", endDay, endTime);

        log.debug("进入判断逻辑");
        if (appointmentStartMode == 2) {
            //可提前startDay天预约课程生效
            log.debug("\n==>全局预约设置:==>可提前{}天预约\n执行appointment_start_mode 2生效", startDay);
            //可提前预约的具体日期时间
            LocalDateTime advanceDateTime = classStartTimeOfSchedule.plusDays(startDay);

            if (appointmentDeadlineMode == 1) {
                log.debug("\n==>全局预约设置:==>不设预约截止时间");
                log.debug("\n执行appointment_start_mode 2 + appointment_deadline_mode 1的模式");
                //只要满足在最早可提前预约时间之后到课程开始之间
                if (nowOperationTime.isAfter(advanceDateTime) && nowOperationTime.isBefore(classStartTimeOfSchedule)) {
                    //满足条件进行更新过新增
                    return addReservation(entity);
                } else {
                    log.debug("\n==>未到可提前预约时间或课程已结束！");
                    return R.error("未到可提前预约时间或课程已结束");
                }
            }


            if (appointmentDeadlineMode == 2) {
                /*
                appointment_start_mode 2 + appointment_deadline_mode 2
                判断当前操作时间是否在模式一和模式二限制内
                */
                log.debug("\n==>全局预约设置:==>预约截止时间：上课前{}小时", endHour);
                log.debug("\n执行appointment_start_mode 2 + appointment_deadline_mode 2的模式");
                //预约截至具体日期时间 截至日期
                LocalDateTime deadline = classStartTimeOfSchedule.minusHours(endHour);

                if (nowOperationTime.isAfter(advanceDateTime) && nowOperationTime.isBefore(deadline)) {
                    //添加或更新
                    return addReservation(entity);
                } else {
                    //未到可提前预约时间或截止时间已过【考虑详细判断】
                    log.debug("\n==>未到可提前预约时间或截止时间已过！");
                    return R.error("未到可提前预约时间或截止时间已过");
                }

            }
            if (appointmentDeadlineMode == 3 && endTime != null) {
                //appointment_start_mode 2 + appointment_deadline_mode 3
                log.debug("\n==>全局预约设置:==>预约截止时间：上课前{}天的{}", endDay, endTime);
                log.debug("\n执行appointment_start_mode 2 + appointment_deadline_mode 3的模式");
                //预约截至具体日期时间,由于给定截止日期的截至时间,所以使用上课的日期LocalData拼上截止时间即为deadline
                LocalDate deadDate = classStartDate.minusDays(endDay);
                LocalDateTime deadline = LocalDateTime.of(deadDate, endTime);

                if (nowOperationTime.isAfter(advanceDateTime) && nowOperationTime.isBefore(deadline)) {
                    //添加或更新
                    return addReservation(entity);
                } else {
                    //未到可提前预约时间或截止时间已过【考虑详细判断】
                    log.debug("\n==>未到可提前预约时间或截止时间已过！");
                    return R.error("未到可提前预约时间或截止时间已过");
                }
            }
        } else {
            log.debug("\n==>全局预约设置:不限制可提前预约的时间");
            if (appointmentDeadlineMode == 2) {
                log.debug("\n==>全局预约设置:==>预约截至时间：上课前{}小时", endHour);
                log.debug("\n执行appointment_start_mode 1 + appointment_deadline_mode 2的模式");
                //appointment_start_mode 1 + appointment_deadline_mode 2
                //预约截至具体时间
                LocalDateTime deadline = classStartTimeOfSchedule.minusHours(endHour);
                if (nowOperationTime.isBefore(deadline)) {
                    //添加或更新
                    return addReservation(entity);
                } else {
                    //超过截止时间
                    log.debug("超出截止时间");
                    return R.error("超出截止时间");
                }
            }
            if (appointmentDeadlineMode == 3 && endTime != null) {
                log.debug("\n==>全局预约设置:==>预约截止时间：上课前{}天的{}", endDay, entity);
                log.debug("\n执行appointment_start_mode 1 + appointment_deadline_mode 3的模式");
                //appointment_start_mode 1 + appointment_deadline_mode 3
                //预约截止具体时间
                LocalDate deadDate = classStartDate.minusDays(endDay);
                LocalDateTime deadline = LocalDateTime.of(deadDate, endTime);
                if (nowOperationTime.isBefore(deadline)) {
                    //添加或更新
                    return addReservation(entity);
                } else {
                    //超过截止时间
                    log.debug("超出截止时间");
                    return R.error("超出截止时间");
                }
            }
            log.debug("\n==>全局预约设置:预约截止时间：不设置预约截止时间");
        }
        //到这里说明，在预约开始和截止时间都不做限制时的添加,需要满足至少在课程开始之前
        if (nowOperationTime.isBefore(classStartTimeOfSchedule)) {
            log.debug("\n执行appointment_start_mode 1 + appointment_deadline_mode 1的模式 \n 需要至少满足在课程开始之前");
            return addReservation(entity);

        } else {
            log.debug("课程已经开始,无法添加预约");
            return R.error("课程已经开始,无法添加预约");
        }
    }

    /**
     * 对数据库的操作
     *
     * @param entity 预约记录
     * @return 添加结果
     */
    @Transactional
    public R addReservation(ReservationRecordEntity entity) {
        log.debug("\n数据库更新操作...");
        //更新或保存预约记录
        boolean isAddReserve = reservationRecordService.saveOrUpdate(entity);
        if (isAddReserve) {
            //取出预约记录的排课记录id
            Long scheduleId = entity.getScheduleId();
            //根据排课记录id查出排课记录完整信息
            ScheduleRecordEntity scheduleById = scheduleRecordService.getById(scheduleId);
            //查出该课程的容纳人数
            Integer contains = courseService.getById(scheduleById.getCourseId()).getContains();
            log.debug("\n==>该课程可容纳的人数==>{}", contains);
            //查出该课程已经预约了多少人
//            int reservedCount = reservationRecordService.count(new QueryWrapper<ReservationRecordEntity>().eq("schedule_id", scheduleId).eq("status", 1));
//            log.debug("\n==>该课程目前已经预约的人数==>{}", reservedCount);
            Integer currentOrderNums = scheduleById.getOrderNums();
            log.debug("\n==>该课程当前已经预约的人数==>{}", currentOrderNums);
            boolean isUpdateReserve = scheduleRecordService.update(new UpdateWrapper<ScheduleRecordEntity>()
                    .eq("id", scheduleId)
                    .set(contains >= currentOrderNums + entity.getReserveNums(), "order_nums", currentOrderNums + entity.getReserveNums()));
            if (isUpdateReserve) {
                //课堂容量够用，可以继续预约
                //先查出使用的绑定会员卡id,即会员卡实体的id       这里的cardId已经改成了会员卡实体id了，即为bindId
//                Long bindId = memberBindRecordService.getOne(new QueryWrapper<MemberBindRecordEntity>().eq("member_id", entity.getMemberId()).eq("card_id", entity.getCardId())).getId();
                Long bindId = entity.getCardId();

                //添加上课信息
                ClassRecordEntity classRecordEntity = new ClassRecordEntity()
                        .setMemberId(entity.getMemberId())
                        .setCardName(entity.getCardName())
                        .setScheduleId(entity.getScheduleId())
                        .setBindCardId(bindId)
                        .setNote("正常预约客户");
                ClassRecordEntity existClassRecord = classRecordService.getOne(new QueryWrapper<ClassRecordEntity>().eq("member_id", entity.getMemberId()).eq("card_name", entity.getCardName()).eq("schedule_id", entity.getScheduleId()));
                if (null != existClassRecord && null != existClassRecord.getId()) {
                    classRecordEntity.setLastModifyTime(LocalDateTime.now()).setVersion(existClassRecord.getVersion() + 1);
                    classRecordEntity = existClassRecord;
                } else {
                    classRecordEntity.setCreateTime(LocalDateTime.now());
                }

                boolean isSaveClassRecord = classRecordService.saveOrUpdate(classRecordEntity);
                log.debug("\n==>添加上课信息是否成功==>{}", isSaveClassRecord);
                if (!isSaveClassRecord) {
                    return R.error("添加上课信息失败");
                }

                return R.ok("预约成功！");
            } else {
                return R.error("该课程容量已满");
            }
        } else {
            return R.error("预约失败");
        }
    }


    /**
     * 用于获取预约id  【上课确认扣费--鼠标离开按钮事件】
     * @param memberId
     * @param scheduleId
     * @return
     */
    @PostMapping("/getReserveId.do")
    @ResponseBody
    public R getReserveId(@RequestParam("memberId") Long memberId,
                          @RequestParam("scheduleId") Long scheduleId) {
        log.debug("\n==>前台传入的会员id==>{}\n==>前台传入的排课记录id==>{}", memberId, scheduleId);
        ReservationRecordEntity reserveByMemberIdAndScheduleId = reservationRecordService.getOne(new QueryWrapper<ReservationRecordEntity>().eq("member_id", memberId).eq("schedule_id", scheduleId));
        log.debug("\n==>打印查出的预约信息==>{}", reserveByMemberIdAndScheduleId);

        Long id = reserveByMemberIdAndScheduleId.getId();
        log.debug("\n==>要返回的预约id==>{}", id);
        return new R().put("id", id);

    }


}
