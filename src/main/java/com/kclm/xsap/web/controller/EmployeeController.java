package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.TeacherClassRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 员工表
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */

@Slf4j
@Controller
@RequestMapping("/user")
public class EmployeeController {

//    private static final String FILEPATH = "upload/images/avatar_img/";
    private static final String FILEPATH = "src/main/resources/static/img/";


    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ClassRecordService classRecordService;

    @Autowired
    private ScheduleRecordService scheduleRecordService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseCardService courseCardService;

    @Autowired
    private MemberCardService memberCardService;


    /**
     * 前往登录页面
     *
     * @return java.lang.string
     * @author fangkai
     * @date 2021/12/4 0004 16:43
     */
    @GetMapping({"/toLogin", "/", "/login"})
    public String toLogin() {
        return "x_login";
    }

    /**
     * @param username
     * @param password
     * @param session
     * @param model
     * @return java.lang.String
     * @author fangkai
     * @date 2021/12/4 0004 16:45
     */
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {
        EmployeeEntity isExistEmp = employeeService.isExistEmp(username, password);
        Boolean flag = (isExistEmp != null);

        model.addAttribute("USER_NOT_EXIST", flag);
        if (flag) {
            session.setAttribute("LOGIN_USER", isExistEmp);
            return "redirect:/index";
        } else {
            return "x_login";
        }

    }


    /**
     * 返回所有老师信息给suggest提供搜索建议
     *
     * @return 所有老师信息
     */
    @GetMapping("/toSearch.do")
    @ResponseBody
    public R toSearch() {
        List<EmployeeEntity> allEmployeeList = employeeService.list();
        log.debug("\n==>返回到前端的所有老师信息allEmployeeList==>{}", allEmployeeList);
        return new R().put("value", allEmployeeList);
    }


    /**
     * 跳转到老师员工管理页面
     *
     * @return x_teacher_list.html
     */
    @GetMapping("/x_teacher_list.do")
    public String togoTeacherList() {
        return "employee/x_teacher_list";
    }


    /**
     * 获取所有员工信息并返回给前端
     *
     * @return R ->员工数据【json】
     */
    @PostMapping("/teacherList.do")
    @ResponseBody
    public R teacherList() {
        List<EmployeeEntity> teachers = employeeService.list();
        return new R().put("data", teachers);
    }

    /**
     * 前端点击编辑后跳转到更新页面
     *
     * @param id 要编辑的id
     * @return x_teacher_update.html
     */
    @GetMapping("/x_teacher_update.do")
    public ModelAndView togoTeacherUpdate(@RequestParam("id") Long id) {
        EmployeeEntity teacherById = employeeService.getById(id);

        log.debug("\n==>根据前端传入的id查询数据库中的员工信息teacherById==>{}", teacherById);
        int samePhoneCount = employeeService.count(new QueryWrapper<EmployeeEntity>().eq("phone", teacherById.getPhone()));

        ModelAndView mv = new ModelAndView();
        if (samePhoneCount > 1) {
            mv.addObject("CHECK_PHONE_EXIST", true);
        }
        mv.setViewName("employee/x_teacher_update");
        mv.addObject("teacherMsg", teacherById);
        mv.addObject("birthdayStr", teacherById.getBirthday());
        log.debug("\n==>即将携带该员工信息跳转到员工编辑更新页面...");
        return mv;
    }


    /**
     * 更新保存员工信息
     *
     * @param entity 前端传入的表单信息封装
     * @return R-> 更新是否成功
     */
    @PostMapping("/teacherEdit.do")
    @ResponseBody
    public R teacherEdit(EmployeeEntity entity) {
        //todo 加入jsr303
        log.debug("\n==>前端传入的要更新的员工信息的封装:entity==>{}", entity);

        //更新操作
        boolean isUpdate = employeeService.updateById(entity);
        log.debug("\n==>更新老师信息的结果==>{}", isUpdate);
        if (isUpdate) {
            return R.ok("更新成功！");
        } else {
            return R.error("更新失败！！");
        }
    }


    /**
     * 跳转老师详情页
     *
     * @param id    老师id
     * @param model 携带老师id
     * @return x_teacher_list_data.html
     */
    @GetMapping("/x_teacher_list_data.do")
    public String togoTeacherListData(@RequestParam("id") Long id, Model model) {
        log.debug("\n==>前台传入的id：==>{}", id);
        model.addAttribute("ID", id);

        return "employee/x_teacher_list_data";
    }


    /**
     * 返回老师详情信息
     * @param id
     * @return
     */
    @PostMapping("/teacherDetail.do")
    @ResponseBody
    public R teacherDetail(@RequestParam("tid") Long id) {
        EmployeeEntity byId = employeeService.getById(id);
        return new R().put("data", byId);
    }


    /**
     * 封装老师管理中的上课记录信息
     * @param id
     * @return
     */
    @PostMapping("/teacherClassRecord.do")
    @ResponseBody
    public R teacherClassRecord(@RequestParam("tid") Long id) {

        List<ScheduleRecordEntity> scheduleForTeacher = scheduleRecordService.list(new QueryWrapper<ScheduleRecordEntity>().eq("teacher_id", id));
        log.debug("\n==>该老师的所有排课计划==>{}", scheduleForTeacher);
        List<TeacherClassRecordVo> teacherClassRecordVos = scheduleForTeacher.stream().map(entity -> {
            Long courseId = entity.getCourseId();
            CourseEntity courseById = courseService.getById(courseId);
            String courseName = courseById.getName();
            log.debug("\n==>课程名==>{}", courseName);
            LocalDateTime classTime = LocalDateTime.of(entity.getStartDate(), entity.getClassTime());
            log.debug("\n==>上课时间==>{}", classTime);
            List<Long> cardIdList = courseCardService.list(new QueryWrapper<CourseCardEntity>().eq("course_id", courseId)).stream().map(CourseCardEntity::getCardId).collect(Collectors.toList());
            Stream<String> cardNameList = memberCardService.listByIds(cardIdList).stream().map(MemberCardEntity::getName);
            String[] cardName = cardNameList.toArray(String[]::new);
            log.debug("\n==>会员卡名集合==>{}\n会员卡名数组{}", cardNameList, cardName);
            Integer timesCost = courseById.getTimesCost();
            log.debug("\n==>使用次数==>{}", timesCost);

            TeacherClassRecordVo vo = new TeacherClassRecordVo().setCourseName(courseName).setClassTime(classTime).setCardName(Arrays.toString(cardName)).setTimesCost(timesCost);
            return vo;
        }).collect(Collectors.toList());

        return new R().put("data", teacherClassRecordVos);
    }


    /**
     * 头像更新
     * todo 回显。。
     * @param id
     * @param file
     * @return
     */
    @PostMapping("/modifyUserImg.do")
    @ResponseBody
    public R modifyUserImg(@RequestParam("id")Long id,
                           @RequestParam("avatarFile") MultipartFile file) {

        if (!file.isEmpty()) {
            log.debug("\n==>文件上传...");
            String fileName = uploadImg(file);
            if (StringUtils.isNotBlank(fileName)) {
                EmployeeEntity entity = new EmployeeEntity().setId(id).setAvatarUrl(fileName).setVersion(+1);
                boolean isUpdateAvatarUrl = employeeService.updateById(entity);
                log.debug("\n==>更新头像是否成功==>{}", isUpdateAvatarUrl);
                return  new R().put("data", entity);
            } else {
                return R.error("文件上传失败");
            }

        }
        return R.error("文件未上传");

    }

    /**
     * 文件上传的方法
     * @param file 传入的文件流
     * @return uuid修改的文件名
     */
    private String uploadImg(MultipartFile file) {
        String projectDir = System.getProperty("user.dir");
        log.debug("\n==>文件上传中打印工程目录==>{}", projectDir);

        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        log.debug("\n==>上传图片的原始名字==>{}", originalFilename);
        //生成一个uuid作为文件的新名字
        String fileName = UUID.randomUUID().toString();

        //获取原始文件的扩展名
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        log.debug("\n==>原始文件的缀名==>{}", suffix);
        //拼接文件新名字
        fileName += suffix;
        log.debug("\n==>上传图片的新名字==>{}", fileName);

        //io
        try {
            File realPath = new File(projectDir, FILEPATH);
            log.debug("\n==>文件的存放地址==>{}", realPath);
            if (!realPath.exists()) {
                log.debug("创建文件目录结构");
                realPath.mkdirs();
            }

            File fileFullPath = new File(realPath, fileName);
            file.transferTo(fileFullPath);
            log.debug("\n==>文件上传成功! ==>文件名：{};文件地址：==>{}", fileName, fileFullPath);
        } catch (IOException e) {
            log.error("头像上传失败！");
            throw new RuntimeException(e);
        }
        return fileName;
    }


    /**
     * 跳转添加老师页面
     * @return x_teacher_add.html
     */
    @GetMapping("/x_teacher_add.do")
    public String togoTeacherAdd(){
        return "employee/x_teacher_add";
    }


    /**
     * 异步添加老师
     * @param entity 前端封装的老师信息
     * @return r->添加是否成功
     */
    @PostMapping("/teacherAdd.do")
    @ResponseBody
    public R teacherAdd(@Valid EmployeeEntity entity, BindingResult bindingResult) {
        log.debug("\n==>前台传入的添加老师表单信息：==>{}", entity);

        if (bindingResult.hasErrors()) {
            HashMap<String, String> map = new HashMap<>();
            bindingResult.getFieldErrors().forEach(item -> {
                String message = item.getDefaultMessage();
                String field = item.getField();
                map.put(field, message);
            });
            return R.error(400, "非法参数").put("errorMap", map);
        }

        entity.setCreateTime(LocalDateTime.now()).setRolePassword("123");
        boolean isSave = employeeService.save(entity);
        log.debug("\n==>保存老师是否成功==>{}", isSave);
        if (isSave) {
            return R.ok("添加成功!");
        }else {
            return R.error("添加失败");
        }
    }



    @PostMapping("/deleteOne.do")
    @ResponseBody
    public R deleteOne(@RequestParam("id") Long id) {
        log.debug("\n==>前端传入的要删除的老师id：==>{}", id);
        boolean isRemove = employeeService.removeById(id);
        log.debug("\n==>删除老师是否成功==>{}", isRemove);

        if (isRemove) {
            return R.ok();
        } else {
            log.debug("\n==>删除老师失败");
            throw new RuntimeException("删除失败！");
        }
    }

}
