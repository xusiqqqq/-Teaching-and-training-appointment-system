package com.kclm.xsap.web.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.kclm.xsap.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.kclm.xsap.entity.GlobalReservationSetEntity;
import com.kclm.xsap.service.GlobalReservationSetService;
import org.springframework.web.servlet.ModelAndView;


/**
 * 全局预约设置表
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@Slf4j
@Controller
@RequestMapping("/globalSet")
public class GlobalReservationSetController {
    @Autowired
    private GlobalReservationSetService globalReservationSetService;


    /**
     * 携带全局预约设置信息跳转到团课预约设置页面
     * @return x_course_reservation.html
     */
    @GetMapping("/x_course_reservation.do")
    public ModelAndView courseReservation() {
        List<GlobalReservationSetEntity> globalReservationSetEntityList = globalReservationSetService.list();
        GlobalReservationSetEntity globalReservationSetEntity = globalReservationSetEntityList.get(0);
        log.debug("\n==>后台查到的全局预约设置是：==>{}", globalReservationSetEntity);
        ModelMap map = new ModelMap();
        map.addAttribute("GLOBAL_SET", globalReservationSetEntity);
        map.addAttribute("endTimeStr", globalReservationSetEntity.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        map.addAttribute("cancelTimeStr", globalReservationSetEntity.getCancelTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        ModelAndView mv = new ModelAndView("course/x_course_reservation", map);
        log.debug("即将跳转到全局团课预约设置页面......");
        return mv;
    }


    /**
     * 更新全局预约设置
     * @param entity 前台传入数据封装
     * @return r -> 更新结果
     */
    @PostMapping("/globalSetUpdate.do")
    @ResponseBody
    public R globalSetUpdate(GlobalReservationSetEntity entity) {
        log.debug("\n==>前端传入的表单封装实体信息为==>{}", entity);
        //当管理员选中的预约开始时间模式是2，并且预约结束时间模式是3时：
        if (entity.getAppointmentStartMode() == 2 && entity.getAppointmentDeadlineMode() == 3) {
            if (entity.getEndDay() > entity.getStartDay()) {
                //如果
                return R.error("预约时间与截止时间设置冲突");
            }
        }
        entity.setVersion(entity.getVersion() + 1).setLastModifyTime(LocalDateTime.now());

        boolean isUpdate = globalReservationSetService.updateById(entity);
        log.debug("\n==>更新全局预约设置是否成功==>{}", isUpdate);
        if (isUpdate) {
            R ok = R.ok("更新成功！");
            log.debug("\n==>ok==>{}", ok);
            return ok;
        } else {
            log.debug("\n==>更新失败。。  ");
            return R.error("更新失败！！");
        }
        //todo 考虑时间是否合理
    }


}
