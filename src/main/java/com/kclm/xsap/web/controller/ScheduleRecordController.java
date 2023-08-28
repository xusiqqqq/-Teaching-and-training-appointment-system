package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.BeanValidationUtils;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.ConsumeFormVo;
import com.kclm.xsap.vo.ScheduleAddValidVo;
import com.kclm.xsap.vo.ScheduleDetailsVo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/schedule")
public class ScheduleRecordController {
    @Resource
    private ScheduleRecordService scheduleService;
    @Resource
    private CourseService courseService;
    @Resource
    private EmployeeService employeeService;

    @Resource
    private CourseCardService courseCardService;

    @Resource
    private ReservationRecordService reservationService;

    @Resource
    private MemberService memberService;

    @Resource
    private ClassRecordService classService;

    @Resource
    private MemberBindRecordService bindService;

    @Resource
    private ConsumeRecordService consumeService;

    @Resource
    private MemberLogService memberLogService;

    @GetMapping("/x_course_schedule.do")
    public String togoCourseSchedule(){
        return "course/x_course_schedule";
    }

    /**
     * 显示排课信息表
     * @return
     */
    @ResponseBody
    @PostMapping("/scheduleList.do")
    public List<Map<String,String>>toScheduleList(){
        List<ScheduleRecordEntity> list = scheduleService.list();
        List<Map<String,String>> mapList=new ArrayList<>();
        for (ScheduleRecordEntity schedule: list) {
            CourseEntity course = courseService.getById(schedule.getCourseId());
            Map<String,String> map=new HashMap<>();
            map.put("title",course.getName()+
                    "「"+employeeService.getById(schedule.getTeacherId()).getName()+"」");
            LocalDateTime start = LocalDateTime.of(schedule.getStartDate(), schedule.getClassTime());
            LocalDateTime end=start.plusMinutes(course.getDuration());
            map.put("start",start.toString());
            map.put("end",end.toString());
            map.put("height","250");
            map.put("color", course.getColor());
            map.put("url","x_course_schedule_detail.do?id="+schedule.getId());
            mapList.add(map);

        }
        return mapList;
    }

    /**
     * 新增排课
     * @param scheduleRecord
     * @param bindingResult
     * @return
     */
    @ResponseBody
    @PostMapping("/scheduleAdd.do")
    public R addSchedule(@Valid ScheduleRecordEntity scheduleRecord, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return BeanValidationUtils.getValidateR(bindingResult);
        LocalDateTime current=LocalDateTime.now();
        scheduleRecord.setCreateTime(current);
        List<ScheduleAddValidVo> validVoList = scheduleService.getScheduleAddValidVo(scheduleRecord);
        if(!validVoList.isEmpty()){
            LocalTime classStartTime=scheduleRecord.getClassTime();
            for (ScheduleAddValidVo vo : validVoList) {
                LocalTime validStartTime=vo.getClassTime();
                LocalTime validEndTime=validStartTime.plusMinutes(courseService.getById(vo.getCourseId()).getDuration());
                if(validEndTime.isAfter(classStartTime)) return R.error("课程冲突，添加排课失败");
            }
        }
        boolean b = scheduleService.save(scheduleRecord);
        if(b) return R.ok("新增课表成功");
        else return R.error("新增课表失败");
    }


    /**
     * 跳转约课的详细信息页面，并传递id，课程限制数量
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/x_course_schedule_detail.do")
    public String togoCourseScheduleDetail(Model model, Long id){

        ScheduleRecordEntity scheduleRecord = scheduleService.getById(id);
        CourseEntity course = courseService.getById(scheduleRecord.getCourseId());
        model.addAttribute("ID",id);
        model.addAttribute("cancelLimitCounts",course.getLimitCounts());
        return "course/x_course_schedule_detail";
    }
    /**
     * 某约课的详细信息
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/scheduleDetail.do")
    public R showScheduleDetail(Long id){
        ScheduleDetailsVo vo = scheduleService.getScheduleDetailsVo(id);
        return new R().put("data",vo);
    }

    /**
     * 通过某课表中的某堂课的scheduleId，获取当前该约课的未取消的预约记录
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/reservedList.do")
    public R showReservedList(Long id){
        return R.ok().put("data",scheduleService.getReserveMapsByScheduleId(id));
    }

    /**
     * 获取该约课所有的预约记录（包括取消的和预约的）
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/reserveRecord.do")
    public R getReserveRecord(Long id){
        return R.ok().put("data",scheduleService.getAllReserveMapsByScheduleId(id));
    }

    /**
     * 获取该约课所有的上课记录
     * @param id
     * @return
     */
    @ResponseBody
    @PostMapping("/classRecord.do")
    public R getClassRecord(Long id){
        return R.ok().put("data",scheduleService.getClassRecordVosByScheduleId(id));
    }

    /**
     *获取单次所使用的会员卡默认单人单节课单次消耗金额
     * @param bindCardId
     * @return
     */
    @ResponseBody
    @PostMapping("/queryAmountsPayable.do")
    public R queryAmountsPayable(Long bindCardId){
        BigDecimal amountsPayable = scheduleService.getAmountsPayable(bindCardId);
        return R.ok().put("data",amountsPayable);
    }

    /**
     * 添加扣费记录和日志记录,先添加日志，因为log是消费的从表。 还需要去修改bind表中的该卡的有效次数,还需要去改classRecord表中的状态
     * @param consumeFormVo
     * @return
     */
    @ResponseBody
    @Transactional
    @PostMapping("/consumeEnsure.do")
    public R consumeEnsure(ConsumeFormVo consumeFormVo){
        LocalDateTime current=LocalDateTime.now();
        ScheduleRecordEntity scheduleRecord = scheduleService.getById(consumeFormVo.getScheduleId());
        LocalDateTime courseStartTime=LocalDateTime.of(scheduleRecord.getStartDate(),scheduleRecord.getClassTime());

        if(current.isBefore(courseStartTime)){
            return R.error(400,"课程开始后，才能进行扣费，请耐心等待");
        }else{
            boolean b = scheduleService.consume(consumeFormVo);
            if(b) return R.ok("扣费成功");
            else return R.error("扣费失败，未知错误，请联系系统管理员");
        }
    }

    /**
     * 一键扣费
     * @param scheduleId
     * @param operator
     * @return
     */
    @ResponseBody
    @Transactional
    @PostMapping("/consumeEnsureAll.do")
    public R consumeEnsureAll(Long scheduleId,String operator){
        LocalDateTime current=LocalDateTime.now();
        ScheduleRecordEntity scheduleRecord = scheduleService.getById(scheduleId);
        LocalDateTime courseStartTime=LocalDateTime.of(scheduleRecord.getStartDate(),scheduleRecord.getClassTime());

        if(current.isBefore(courseStartTime)){
            return R.error(400,"课程开始后，才能进行扣费，请耐心等待");
        }else{
            //查出预约成功，并且未上课的学生。
            LambdaQueryWrapper<ClassRecordEntity> qw=new LambdaQueryWrapper<>();
            qw.eq(ClassRecordEntity::getScheduleId,scheduleId).eq(ClassRecordEntity::getReserveCheck,1).eq(ClassRecordEntity::getCheckStatus,0);
            List<ClassRecordEntity> list = classService.list(qw);
            if(list.isEmpty()) return R.error(400,"该课程已经扣费完毕，请勿重复操作");
            for (ClassRecordEntity classRecord : list) {
                ConsumeFormVo vo=new ConsumeFormVo();
                vo.setClassId(classRecord.getId());
                vo.setScheduleId(classRecord.getScheduleId());
                vo.setCardBindId(classRecord.getBindCardId());
                vo.setMemberId(classRecord.getMemberId());
                vo.setOperator(operator);
                vo.setNote("一键扣费操作");
                //获单次的均价
                BigDecimal amountsPayable = scheduleService.getAmountsPayable(classRecord.getBindCardId());
                //获取课程所需消卡次数
                ReservationRecordEntity reserveRecord = classService.getReserveByClassRecord(classRecord);
                Integer courseCostTimes = scheduleService.getCourseCostTimes(scheduleId);
                //获取预约人数
                Integer reserveNums = reserveRecord.getReserveNums();
                vo.setAmountOfConsumption(amountsPayable.multiply(BigDecimal.valueOf((long) courseCostTimes*reserveNums)));
                vo.setCardCountChange(courseCostTimes*reserveNums);
                if(!scheduleService.consume(vo)) return R.error("批量扣费失败");
            }
            return R.ok("批量扣费成功");
        }

    }

    /**
     * 查询2周类的排课记录
     * @return
     */
    @ResponseBody
    @GetMapping("/toSearch.do")
    public R toSearch(){
        return R.ok().put("value",scheduleService.getScheduleForConsumeSearchVo(LocalDateTime.now()));
    }

    /**
     * 删除约课
     * @param id
     * @return
     */
    @Transactional
    @ResponseBody
    @PostMapping("/deleteOne.do")
    public R deleteOne(Long id){
        int count = reservationService.count(new LambdaQueryWrapper<ReservationRecordEntity>().eq(ReservationRecordEntity::getScheduleId, id));
        if(count>0) return R.error("该课程存在预约记录，无法删除");
        boolean b = scheduleService.removeById(id);
        if(b) return  R.ok("删除成功");
        else return R.error("删除失败，未知错误");
    }


    /**
     * 复制排课信息，如果目标日期比被复制的日期早  目标日期等于被复制的日期  冲突课程
     * @param sourceDateStr
     * @param targetDateStr
     * @return
     */
    @ResponseBody
    @PostMapping("/scheduleCopy.do")
    public R copySchedule(@RequestParam("sourceDateStr")String sourceDateStr,@RequestParam("targetDateStr")String targetDateStr){
        LocalDate sourceDate=LocalDate.parse(sourceDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate targetDate=LocalDate.parse(targetDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate current=LocalDate.now();

        if(targetDate.isBefore(current)||sourceDate.isEqual(targetDate)) return R.error("不能将课表复制到过去的时间");
        LambdaQueryWrapper<ScheduleRecordEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(ScheduleRecordEntity::getStartDate,sourceDate);
        //被复制的课程
        List<ScheduleRecordEntity> list = scheduleService.list(qw);
        //如果为空，直接返回被复制日期没有排课记录信息
        if(list.isEmpty())return R.error("被复制日期没有排课记录");

        //能被复制的排课list
        List<ScheduleRecordEntity> resultList = scheduleService.getScheduleRecordEntities(targetDate, list);
        if(resultList.isEmpty()) return R.error("所有课程均冲突，无法复制");
        if (scheduleService.saveBatch(resultList)) return R.ok("复制成功,共"+list.size()+"条排课记录，成功"+resultList.size()+"条，冲突"+(list.size()-resultList.size())+"条");
        return R.error("未知错误");
    }



}
