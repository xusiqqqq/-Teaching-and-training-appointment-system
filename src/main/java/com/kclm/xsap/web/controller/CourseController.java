package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kclm.xsap.entity.CourseCardEntity;
import com.kclm.xsap.entity.CourseEntity;
import com.kclm.xsap.entity.ScheduleRecordEntity;
import com.kclm.xsap.service.CourseCardService;
import com.kclm.xsap.service.CourseService;
import com.kclm.xsap.service.ScheduleRecordService;
import com.kclm.xsap.utils.BeanValidationUtils;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.CourseLimitVo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController {
    @Resource
    private CourseService courseService;
    @Resource
    private CourseCardService courseCardService;

    @Resource
    private ScheduleRecordService scheduleService;


    /**
     * 显示所有的课程信息
     * @return List<CourseEntity>
     */
    @ResponseBody
    @PostMapping("/courseList.do")
    public List<CourseEntity> getAllCourse(){
        return  courseService.list();
    }

    /**
     * 跳转课程列表页面
     * @return
     */
    @GetMapping("/x_course_list.do")
    public String togoCourseList(){
        return "course/x_course_list";
    }

    /**
     * 跳转课程编辑页面
     * @param model
     * @param id
     * @return "course/x_course_list_edit"
     */
    @GetMapping("/x_course_list_edit.do")
    public String togoCourseList(Model model, Long id){
        //根据课程id，获取该课程能够使用的全部会员卡id
        List<CourseCardEntity> courseCardList = courseCardService.list(new LambdaQueryWrapper<CourseCardEntity>().eq(CourseCardEntity::getCourseId, id));
        List<Long> cardIds=new ArrayList<>();
        for (CourseCardEntity courseCard : courseCardList) cardIds.add(courseCard.getCardId());
        model.addAttribute("cardCarry",cardIds);
        //根据课程id，获取课程的详细信息
        model.addAttribute("id",id);
        model.addAttribute("courseInfo",courseService.getById(id));
        return "course/x_course_list_edit";
    }

    /**
     * 课程编辑
     * @param course
     * @param bindingResult
     * @param cardListStr
     * @param limitAgeRadio
     * @param limitCountsRadio
     * @return
     */
    @Transactional
    @ResponseBody
    @PostMapping("/courseEdit.do")
    public R editCourse(@Valid CourseEntity course,BindingResult bindingResult,Long[] cardListStr,Integer limitAgeRadio,Integer limitCountsRadio){
        if (bindingResult.hasErrors()) return BeanValidationUtils.getValidateR(bindingResult);

        //1、更新课程的详细信息
        if (limitAgeRadio == -1) course.setLimitAge(0);
        else {
            if (course.getLimitAge() < 3) return R.error("课程限制年龄不能小于3");
        }
        if (limitCountsRadio == -1) course.setLimitCounts(0);
        else {
            if (course.getLimitCounts() < 1) return R.error("课程限制预约次数不能小于1");
        }
        course.setLastModifyTime(LocalDateTime.now());
        boolean b1 = courseService.updateById(course);
        //2、更新联系，先删除 课程-会员卡表 中的信息，在调用saveCourse()
        courseCardService.remove(new LambdaQueryWrapper<CourseCardEntity>().eq(CourseCardEntity::getCourseId, course.getId()));
        boolean b2 = courseService.saveCourseCard(course, cardListStr);
        if (b1 && b2) return R.ok("课程信息修改成功");
        else return R.error("课程信息修改失败");

    }

    /**
     * 新增课程信息
     * @return
     */
    @GetMapping("/x_course_list_add.do")
    public String toCourseListAdd(){
        return "course/x_course_list_add";
    }
    @Transactional
    @ResponseBody
    @PostMapping("/courseAdd.do")
    public R addCourse(@Valid CourseEntity course,BindingResult bindingResult,Long[] cardListStr){
        if(bindingResult.hasErrors()) return BeanValidationUtils.getValidateR(bindingResult);
        else{
            //1、存入课程详细信息
            course.setCreateTime(LocalDateTime.now());
            course.setLastModifyTime(LocalDateTime.now());
            boolean b1 = courseService.save(course);
            //2、存入两者联系，调用saveCourse()
            boolean b2 = courseService.saveCourseCard(course, cardListStr);
            if(b1&&b2) return R.ok("课程信息添加成功");
            else return R.error("课程信息添加失败");
        }
    }


    /**
     * 跳转到课程表
     * @return
     */
    @GetMapping("/x_course_schedule.do")
    public String togoCourseSchedule(){
        return "course/x_course_schedule";
    }

    /**
     * 搜索课程，前端使用bsSuggest控件，注意返回值应为key="value"
     * @return
     */
    @ResponseBody
    @GetMapping("/toSearch.do")
    public R toSearch(){
        return new R().put("value",courseService.list());
    }

    /**
     * 根据课程id获取CourseLimitVo
     * @param id 课程id
     * @return
     */
    @ResponseBody
    @PostMapping("/getOneCourse.do")
    public R getOneCourse(Long id){
        CourseEntity course= courseService.getById(id);
        CourseLimitVo vo=new CourseLimitVo();
        vo.setLimitAge(course.getLimitAge());
        vo.setLimitSex(course.getLimitSex());
        //课程时长
        vo.setDuration(course.getDuration());
        return R.ok().put("data",vo);
    }

    /**
     * 课程删除，还需要删除绑定的会员卡信息
     * @param id 课程id
     * @return
     */
    @Transactional
    @ResponseBody
    @PostMapping("/deleteOne.do")
    public R deleteOne(Long id){
        int count = scheduleService.count(new LambdaQueryWrapper<ScheduleRecordEntity>().eq(ScheduleRecordEntity::getCourseId, id));
        if(count>0) return R.error("该课程有排课信息，无法删除");
        boolean b = courseService.removeById(id);
        if(b) return R.ok("删除成功");
        else return R.error("删除失败");
    }

}
