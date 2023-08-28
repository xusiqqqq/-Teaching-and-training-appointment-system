package com.kclm.xsap.web.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.utils.exception.ReserveAddException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reserve")
public class ReservationRecordController {
    @Resource
    private ReservationRecordService reservationService;

    @Resource
    private GlobalReservationSetService globalReservationSetService;

    @Resource
    private CourseService courseService;

    @Resource
    private ScheduleRecordService scheduleRecordService;

    @Resource
    private MemberCardService memberCardService;

    @Resource
    private MemberService memberService;

    @Resource
    private MemberBindRecordService memberBindRecordService;

    @Resource
    private ClassRecordService classRecordService;

    @Resource
    private CourseCardService courseCardService;


    /**
     * 根据会员id，课表id获取预约记录
     * @param memberId
     * @param scheduleId
     * @return
     */
    @ResponseBody
    @PostMapping("/getReserveId.do")
    public ReservationRecordEntity getReserveId(Long memberId,Long scheduleId){
        LambdaQueryWrapper<ReservationRecordEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(ReservationRecordEntity::getMemberId,memberId)
                .eq(ReservationRecordEntity::getScheduleId,scheduleId)
                .eq(ReservationRecordEntity::getStatus,1);
        return reservationService.getOne(qw);
    }

    /**
     * 课程预约
     * @param reservationRecord
     * @return
     */
    @ResponseBody
    @Transactional
    @PostMapping("/addReserve.do")
    public R addReserve(ReservationRecordEntity reservationRecord){
        //获取当前日期时间
        LocalDateTime currentTime=LocalDateTime.now();
        reservationRecord.setCreateTime(currentTime);

        //获取预约全局配置信息
        GlobalReservationSetEntity globalReservationSet = globalReservationSetService.list().get(0);

        //获取当前课程schedule
        ScheduleRecordEntity scheduleRecord = scheduleRecordService.getById(reservationRecord.getScheduleId());

        //获取课程course
        CourseEntity course = courseService.getById(scheduleRecordService.getById(reservationRecord.getScheduleId()).getCourseId());

        //获取会员对象member
        MemberEntity member = memberService.getById(reservationRecord.getMemberId());


        //获取会员卡信息card
        MemberCardEntity card=memberCardService.getById(reservationRecord.getCardId());

        //获取绑卡信息bindRecord
        LambdaQueryWrapper<MemberBindRecordEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(MemberBindRecordEntity::getMemberId,reservationRecord.getMemberId()).eq(MemberBindRecordEntity::getCardId,reservationRecord.getCardId());
        MemberBindRecordEntity bindRecord = memberBindRecordService.getOne(qw);

        //获取到课程开始时间
        LocalDateTime courseStartTime=LocalDateTime.of(scheduleRecord.getStartDate(),scheduleRecord.getClassTime());

        //预约信息的校验
        try {
            //验证当前时间是否能进行预约
            reservationService.validReserveTime(currentTime,globalReservationSet,courseStartTime);
            //验证当前的用户会员卡是否过期或者能否支持上这门课
            reservationService.validReserveCard(course, card, bindRecord, courseStartTime);
            //课程层面，课程本身的限制，课堂的容纳人数，最多取消预约次数，性别，年龄
            reservationService.validReserveCourse(reservationRecord, currentTime, scheduleRecord, course, member);
            //如果用户已经预约过该课程，则进行提醒
            reservationService.validReserveMember(reservationRecord);

        } catch (ReserveAddException e) {
            return e.getR();
        }

        //可以预约,添加预约的时候
        // 添加ClassRecord记录
        ClassRecordEntity classRecord=new ClassRecordEntity();
        classRecord.setMemberId(member.getId());
        classRecord.setCardName(card.getName());
        classRecord.setScheduleId(scheduleRecord.getId());
        classRecord.setCheckStatus(0);//0表示未上课 1表示已经上课
        classRecord.setReserveCheck(1);
        classRecord.setCreateTime(currentTime);
        classRecord.setBindCardId(bindRecord.getId());
        classRecord.setNote("正常预约的客户");
        classRecordService.save(classRecord);

        // 添加ReservationRecord
        //需要将该课程的预约人数+1,更新
        scheduleRecord.setOrderNums(scheduleRecord.getOrderNums()+reservationRecord.getReserveNums());
        scheduleRecordService.updateById(scheduleRecord);
        //还需要设置预约的状态 1表示预约，0表示取消
        reservationRecord.setStatus(1);
        boolean b = reservationService.save(reservationRecord);
        if(b)return R.ok("预约课程成功");
        else return R.error("预约课程失败");

    }


    /**
     * 取消预约：需要在取消预约截止时间前  还需要update classRecord记录
     * @param reserveId
     * @return
     */
    @ResponseBody
    @Transactional
    @PostMapping("/cancelReserve.do")
    public R cancelReserve(Long reserveId){
        //获取当前日期时间
        LocalDateTime currentTime=LocalDateTime.now();
        //获取预约全局配置信息
        GlobalReservationSetEntity globalReservationSet = globalReservationSetService.list().get(0);
        //获取预约记录
        ReservationRecordEntity reservationRecord = reservationService.getById(reserveId);
        //获取上课课程信息
        ScheduleRecordEntity scheduleRecord = scheduleRecordService.getById(reservationRecord.getScheduleId());
        //获取到课程开始时间
        LocalDateTime courseStartTime=LocalDateTime.of(scheduleRecord.getStartDate(),scheduleRecord.getClassTime());

        //获取全局取消预约的截止时间
        LocalDateTime cancelTime=courseStartTime;
        Integer cancelMode = globalReservationSet.getAppointmentCancelMode();
        if(cancelMode==2)
            cancelTime=courseStartTime.plusHours(-globalReservationSet.getCancelHour());
        if(cancelMode==3){
            LocalTime cancelHourAndMin = globalReservationSet.getCancelTime();
            cancelTime=courseStartTime
                    .plusDays(-globalReservationSet.getCancelDay())
                    .plusHours(-cancelHourAndMin.getHour())
                    .plusHours(-cancelHourAndMin.getMinute());
        }

        if(currentTime.isAfter(cancelTime))
            return R.error("已过取消预约截止时间，无法取消");

        //schedule:设置当前课程预约人数-取消预约的人数
        scheduleRecord.setOrderNums(scheduleRecord.getOrderNums()-reservationRecord.getReserveNums());
        scheduleRecord.setLastModifyTime(currentTime);
        boolean b1 = scheduleRecordService.updateById(scheduleRecord);

        //reservation:设置预约状态为0，表示未预约
        reservationRecord.setStatus(0);
        reservationRecord.setCancelTimes(reservationRecord.getCancelTimes()+1);
        boolean b2 = reservationService.updateById(reservationRecord);

        //直接删除上课记录
        LambdaQueryWrapper<ClassRecordEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(ClassRecordEntity::getScheduleId,scheduleRecord.getId())
                .eq(ClassRecordEntity::getMemberId,reservationRecord.getMemberId());
        boolean b3 = classRecordService.remove(qw);


        if(b1&&b2&&b3) return R.ok();
        return R.error("发生未知错误，请联系系统管理员");
    }

    /**
     * 导出startDate~endDate的预约记录
     * @param startDateStr
     * @param endDateStr
     * @param response
     * @throws IOException
     */
    @ResponseBody
    @GetMapping("/exportReserveList.do")
    public void exportReserveList(String startDateStr, String endDateStr, HttpServletResponse response) throws IOException {
        LocalDate start = LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LambdaQueryWrapper<ReservationRecordEntity> qw = new LambdaQueryWrapper<>();
        qw.between(ReservationRecordEntity::getCreateTime, start, end);
        List<ReservationRecordEntity> list = reservationService.list(qw);
        ExcelWriter writer = ExcelUtil.getWriter(true);

        List<Map<String,String>> mapList=new ArrayList<>();
        for (ReservationRecordEntity reserveRecord : list) {
            Map<String,String> map= MapUtil.newHashMap(true);
            map.put("预约id"  ,  reserveRecord.getId().toString()     );
            map.put("会员"  ,memberService.getById(reserveRecord.getMemberId()).getName());
            map.put("会员卡"  , reserveRecord.getCardName()       );
            ScheduleRecordEntity scheduleRecord = scheduleRecordService.getById(reserveRecord.getScheduleId());
            map.put("课程"  ,courseService.getById(scheduleRecord.getCourseId()).getName() );
            map.put("预约人数"  ,  reserveRecord.getReserveNums().toString()     );
            map.put("操作员"  ,     reserveRecord.getOperator()  );
            map.put("上课备注"  ,    reserveRecord.getNote()   );
            map.put("预约状态"  , reserveRecord.getStatus()==0?"无效":"有效" );
            map.put("上课时间", LocalDateTime.of(scheduleRecord.getStartDate(),scheduleRecord.getClassTime()).format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分ss秒")));
            map.put("预约时间",reserveRecord.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分ss秒")));
            mapList.add(map);
        }

        //默认配置
        writer.write(mapList,true);
        //设置content—type
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset:utf-8");
        //设置标题
        //Content-disposition是MIME协议的扩展，MIME协议指示MIME用户代理如何显示附加的文件。
        String fileName = URLEncoder.encode(startDateStr+"~"+endDateStr+"预约记录表", "UTF-8");    // 设置名称和字符集
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        ServletOutputStream outputStream = response.getOutputStream();

        //将Writer刷新到OutPut
        writer.flush(outputStream,true);
        outputStream.close();
        writer.close();
    }
}
