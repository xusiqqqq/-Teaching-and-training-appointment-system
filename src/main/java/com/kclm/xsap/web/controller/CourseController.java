package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.entity.CourseCardEntity;
import com.kclm.xsap.entity.CourseEntity;
import com.kclm.xsap.service.CourseCardService;
import com.kclm.xsap.service.CourseService;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.CourseLimitVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 课程表
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */

@Slf4j
@Controller
@RequestMapping("/course")
public class CourseController {

    private static final String SQLSTATE_23000 = "23000";

    @Autowired
    private CourseService courseService;


    @Autowired
    private CourseCardService courseCardService;


    /**
     * 前端通过ajax请求所有课程列表
     *
     * @return 返回所有课程列表-json
     */
    @PostMapping("/courseList.do")
    @ResponseBody
    public List<CourseEntity> getAllCourse() {
        List<CourseEntity> courseEntityList = courseService.list();
        log.debug("\n 返回所有课程列表courseEntityList==>>{}", courseEntityList);

        return courseEntityList;
    }

    /**
     * 跳转课程添加页面
     *
     * @return x_course_list_add.html
     */
    @GetMapping("/x_course_list_add.do")
    public String courseListAdd() {
        log.debug("\n==>跳转到课程添加页面...==>");
        return "/course/x_course_list_add";
    }


    /**
     * 跳转课程列表页面
     *
     * @return x_course_list.html
     */
    @GetMapping("/x_course_list.do")
    public String courseList() {
        return "/course/x_course_list";
    }

    /**
     * 跳转某课程编辑页面
     *
     * @param id 该课程的id
     * @return x_course_list_edit.html
     */
    @GetMapping("/x_course_list_edit.do")
    public ModelAndView courseListEdit(@RequestParam("id") Integer id) {
        log.debug("\n==>前端传入的要编辑的id是id==>{}", id);
        CourseEntity courseEntityById = courseService.getById(id);
        log.debug("\n==>根据id查出的课程信息：courseEntityById==>{}", courseEntityById);

        List<CourseCardEntity> courseCardByCourseId = courseCardService.list(new QueryWrapper<CourseCardEntity>().eq("course_id", id));
        log.debug("后端查询：对应课程支持关联的会员卡：courseCardByCourseId==>{}", courseCardByCourseId);

        List<Long> cardIds = courseCardByCourseId.stream().map(CourseCardEntity::getCardId).collect(Collectors.toList());
        log.debug("\n==>根据查询到的courseCardByCourse的list返回它的会员卡id集合==>{}", cardIds);

        ModelMap map = new ModelMap();
        map.addAttribute("cardCarry", cardIds);
        map.addAttribute("courseInfo", courseEntityById);


        ModelAndView mv = new ModelAndView("/course/x_course_list_edit", map);
        log.debug("\n==>跳转课程编辑页面==>x_course_list_edit.html");
        return mv;
    }


    /**
     * 异步添加保存提交的课程信息
     *
     * @param courseEntity 表单提交的课程信息
     * @param cardListStr 表单提交的支持的会员卡
     * @return r -> 添加结果
     */
    @PostMapping("/courseAdd.do")
    @ResponseBody
    @Transactional
    public R courseAdd(@Valid CourseEntity courseEntity, BindingResult bindingResult,
                       String cardListStr) {

        log.debug("\n课程表和课程会员卡关联表分开提交，先提交课程表;\n==>前端传入的课程信息封装结果courseEntity==>{}", courseEntity);
        if (bindingResult.hasErrors()) {
            HashMap<String, String> map = new HashMap<>();
            bindingResult.getFieldErrors().forEach(item -> {
                String message = item.getDefaultMessage();
                String field = item.getField();
                map.put(field, message);
            });
            return R.error(400, "非法参数！！").put("errorMap", map);
        }

        courseEntity.setCreateTime(LocalDateTime.now()).setVersion(courseEntity.getVersion() + 1);
        boolean isInsertCourse = courseService.save(courseEntity);
        log.debug("\n==>添加课程信息是否成功==>{}\n 尝试获取关联会员卡信息并提交", isInsertCourse);

        if (StringUtils.isBlank(cardListStr)) {
            //会员卡信息为空
            log.debug("\n==>未提交会员卡信息,忽略后返回保存信息==>");
            return R.ok("课程信息提交成功！【未设置课程可支持的会员卡信息】");
        } else {
            log.debug("\n==>前端传入的课程关联的支持的会员卡信息cardListStr==>{}", cardListStr);
            List<Long> cardIdsFromPage = Arrays.stream(cardListStr.split(",")).map(Long::valueOf).collect(Collectors.toList());
            if (cardIdsFromPage.contains(-1L)) {
                log.debug("\n==>用户勾选了全选支持的会员卡【全选会在第一个位置传入-1");
                boolean remove = cardIdsFromPage.remove(-1L);
                log.debug("删除的-1是否成功：{}", remove);
            }
            List<CourseCardEntity> courseCardEntityList = cardIdsFromPage.stream().map(id ->
                    new CourseCardEntity().setCourseId(courseEntity.getId()).setCardId(id)
            ).collect(Collectors.toList());
            log.debug("\n==>通过传入的courseEntity和cardListStr映射封装的courseCardEntity==>{}\n 保存关联表信息...", courseCardEntityList);
            //保存关联信息,由于新添加的课程一定不会与已有课程冲突，所以直接插入
            boolean isInsertCourseCard = courseCardService.saveCourseCard(courseCardEntityList);
            log.debug("\n==>保存课程关联的可支持的会员卡是否成功==>{}", isInsertCourseCard);
        }
        return R.ok("课程信息保存成功");

    }


    /**
     * 异步保存课程编辑修改
     *
     * @param courseEntity 前端传入的课程类的封装
     * @param cardListStr  前端传入的课程关联会员卡信息
     * @return 统一返回R是否成功
     */
    @PostMapping("/courseEdit.do")
    @ResponseBody
    @Transactional
    public R courseEdit(@Valid CourseEntity courseEntity, BindingResult bindingResult, String cardListStr) {
        log.debug("\n==>前台更新表单提交的信息courseEntity:==>{}", courseEntity);
        if (bindingResult.hasErrors()) {
            HashMap<String, String> map = new HashMap<>();
            bindingResult.getFieldErrors().forEach(item -> {
                String message = item.getDefaultMessage();
                String field = item.getField();
                map.put(field, message);
            });
            return R.error(400, "非法参数").put("errorMap", map);
        }
        //todo 前端页面还没有修改

        //在数据库中查出对应的课程支持的会员卡
        List<CourseCardEntity> courseCardListByCourseId = courseCardService.list(new QueryWrapper<CourseCardEntity>().eq("course_id", courseEntity.getId()));
        log.debug("\n==>数据库中该课程关联的可支持的课程集合courseCardListByCourseId==>{}", courseCardListByCourseId);
        List<Long> cardIdListFromDB = courseCardListByCourseId.stream().map(CourseCardEntity::getCardId).sorted().collect(Collectors.toList());
        log.debug("\n==>由上述courseCardListByCourseId流式映射出的该课程关联的可支持的课程i的d集合cardIdList==>{}", cardIdListFromDB);

        //取出数据库中该课程关联的可支持的会员卡是否为空，ture表示非空，false表示为空
        boolean isNotEmpty = !cardIdListFromDB.isEmpty();
        log.debug("\n==>数据库中该课程关联的可支持的会员卡是否为空==>{}", isNotEmpty);

        log.debug("\n==>前端传入的数据封装成courseEntity==>{}", courseEntity);
        //先更新课程信息
        log.debug("\n==>由于要更新的数据在两个表中，先更新课程表相关信息，再更新课程-会员卡关联表信息");
        //设置更新时间和版本
        courseEntity.setLastModifyTime(LocalDateTime.now()).setVersion(courseEntity.getVersion() + 1);
        boolean isUpdateCourse = courseService.updateById(courseEntity);
        log.debug("\n==>更新course信息到数据库是否成功==>{}\n即将尝试更新关联表", isUpdateCourse);

        //更新关联的支持的会员卡信息
        if (StringUtils.isNotBlank(cardListStr)) {
            //1.传入的cardListStr不为null
            log.debug("\n==>此时前端传入的该课程关联的支持的会员卡不为空且值cardListStr为==>{}", cardIdListFromDB);
            //分解成str数组
            String[] split = cardListStr.split(",");
            //将str数组再流式转化成会员卡id集合
            List<Long> cardIdListFromPage = Arrays.stream(split).map(Long::valueOf).sorted().collect(Collectors.toList());
            log.debug("\n==>前端传入的该课程关联的可支持会员卡转成list集合为cardIdListFromPage==>{}", cardIdListFromPage);
            if (cardIdListFromPage.toString().equals(cardIdListFromDB.toString())) {
                log.debug("\n==>用户未修改关联可支持会员卡信息，即将直接返回");
//                return new R().put("data", "更新成功！课程关联的会员卡未修改");
                return R.ok("更新成功！课程关联的会员卡未修改");
            }
            //赋值-->将传入的courseEntity中的id和上文得到的cardIdListFromPage通过流式映射成List<CourseCardEntity>
            List<CourseCardEntity> courseCardEntityList = cardIdListFromPage.stream().map(item ->
                    new CourseCardEntity().setCourseId(courseEntity.getId()).setCardId(item)
            ).collect(Collectors.toList());
            log.debug("\n==>将传入的courseEntity中的id和上文得到的cardIdListFromPage通过流式映射成List<CourseCardEntity> 其值为：==>{}", courseCardEntityList);
            //调用CourseCardService处理更新
            //2.数据库中课程可支持会员卡不为空
            if (isNotEmpty) {
                log.debug("数据库中该课程关联的可支持的会员卡不为空,即将删除原本的可支持的会员卡信息...");
                boolean isDeleteAllCardByCourseId = courseCardService.remove(new QueryWrapper<CourseCardEntity>().eq("course_id", courseEntity.getId()));
                log.debug("删除所有该课程对应所有会员卡是否成功：isDelete:{}", isDeleteAllCardByCourseId);
            } else {
                log.debug("\n==>数据可中该课程关联的可支持的会员卡为空");
            }
            log.debug("\n==>即将添加该课程可以支持的课程");
            boolean isInsertCourseCard = courseCardService.saveCourseCard(courseCardEntityList);
            log.debug("\n==>添加课程对应会员卡的关联信息是否成功==>{}", isInsertCourseCard);
        } else {
            log.debug("\n==>传入的cardListStr为空");
            if (isNotEmpty) {
                //如果数据库中不为空，则可以删除
                log.debug("\n==>数据库中课程对应关联会员卡不为空isNotEmpty\t说明可以直接删除");
                boolean isDelete = courseCardService.removeById(courseEntity.getId());
                log.debug("\n==>删除数据库中课程对应关联的会员卡是否成功==>{}", isDelete);
            } else {
                //数据也为空，无需做任何操作,其实到不了这里，会被前面的相等判断返回
                log.debug("\n==>数据库也为空，说明无需任何处理isNotEmpty");
            }
        }

        log.debug("修改更新支持的会员卡成功");

//        return new R().put("data", "修改成功");
        return R.ok("修改成功");
    }


    /**
     * 1.通过异常捕获到的异常类型，判断删除是否成功//由于可能也是课程会员卡绑定表的外键，所以行不通了
     * 2.这里还是通过线衫课程会员卡绑定表再删课程表
     *
     * @param id 要删除的课程id
     * @return 统一返回R成功或失败
     */
    @PostMapping("/deleteOne.do")
    @ResponseBody
    @Transactional
    public R deleteOne(@RequestParam("id") Integer id) {
        //此课程已经生成了预约、上课、排课等记录，不可被删除！
        //由于预约记录和上课记录都与排课记录构成外键约束，而排课记录表又与课程表构成外键约束，所以可以尝试直接删除课程表，成功即可删，失败即不可删

        boolean isRemoveCourseCard = courseCardService.remove(new QueryWrapper<CourseCardEntity>().eq("course_id", id));
        log.debug("\n==>删除课程与会员卡绑定信息是否成功==>{}", isRemoveCourseCard);
        boolean isRemoveCourse = false;

        try {
            isRemoveCourse = courseService.removeById(id);

            log.debug("\n==>删除课程信息是否成功==>{}", isRemoveCourse);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLIntegrityConstraintViolationException) {
                String sqlState = ((SQLIntegrityConstraintViolationException) cause).getSQLState();
                if (SQLSTATE_23000.equals(sqlState)) {
                    return R.error("删除失败!此课程已经生成了预约、上课、排课等记录，不可被删除！");
                }
            }
        }

        /*if (!isRemoveCourse) {
            return R.error("删除失败!此课程已经生成了预约、上课、排课等记录，不可被删除！");
        }*/

        if (!isRemoveCourse) {
            return R.ok("删除成功！即将跳转课程列表");
        }

        return R.error("删除失败");

    }


    /**
     * 返回所有课程信息给suggest提供搜索建议
     *
     * @return 所有课程信息
     */
    @GetMapping("/toSearch.do")
    @ResponseBody
    public R toSearch() {
        List<CourseEntity> allCourseList = courseService.list();
        log.debug("\n==>返回给前端的课程列表allCourseList：==>{}", allCourseList);
        return new R().put("value", allCourseList);
    }


    /**
     * 新增排课课程表中获取指定课程的限制信息
     *
     * @param id 指定课程id
     * @return R->封装限制信息
     */
    @PostMapping("/getOneCourse.do")
    @ResponseBody
    public R getOneCourse(@RequestParam("id") Integer id) {
        log.debug("\n==>前端传入的id==>{}", id);
        CourseEntity courseEntityById = courseService.getById(id);

        CourseLimitVo limitVo = new CourseLimitVo();
        BeanUtils.copyProperties(courseEntityById, limitVo);

        return new R().put("data", limitVo);
    }


}
