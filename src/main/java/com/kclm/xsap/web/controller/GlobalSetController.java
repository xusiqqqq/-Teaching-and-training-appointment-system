package com.kclm.xsap.web.controller;

import com.kclm.xsap.entity.GlobalReservationSetEntity;
import com.kclm.xsap.service.GlobalReservationSetService;
import com.kclm.xsap.utils.R;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/globalSet")
public class GlobalSetController {
    @Resource
    private GlobalReservationSetService globalSetService;

    /**
     * 跳转全局预约设置页面
     * @param model
     * @return
     */
    @GetMapping("/x_course_reservation.do")
    public String togoCourseReservation(Model model){
        model.addAttribute("GLOBAL_SET",globalSetService.list().get(0));

        return "course/x_course_reservation";
    }

    /**
     * 修改全局预约设置页面
     * @param globalReservationSet
     * @return
     */
    @ResponseBody
    @PostMapping("/globalSetUpdate.do")
    public R updateGlobalSet(GlobalReservationSetEntity globalReservationSet){
        globalReservationSet.setId(1l);
        globalReservationSet.setVersion(globalSetService.getById(1l).getVersion()+1);
        boolean b = globalSetService.updateById(globalReservationSet);
        if(b) return R.ok("全局预约设置保存成功");
        else return R.error("全局预约设置保存失败");
    }

}
