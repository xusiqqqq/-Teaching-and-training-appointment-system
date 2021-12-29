package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.consts.OperateType;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.BindCardInfoVo;
import com.kclm.xsap.vo.CardInfoVo;
import com.kclm.xsap.vo.OperateRecordVo;
import com.kclm.xsap.vo.ReserveFormCountVo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 会员卡表
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */

@Slf4j
@Controller
@RequestMapping("/card")
public class MemberCardController {
    @Autowired
    private MemberCardService memberCardService;

    @Autowired
    private CourseCardService courseCardService;

    @Autowired
    private MemberBindRecordService memberBindRecordService;

    @Autowired
    private ScheduleRecordService scheduleRecordService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private MemberLogService memberLogService;

    @Autowired
    private ConsumeRecordService consumeRecordService;


    /**
     * 返回会员卡列表
     *
     * @return 会员卡列表
     */
    @PostMapping("/cardList.do")
    @ResponseBody
    public R cardList() {
        List<MemberCardEntity> list = memberCardService.list();
        log.debug("\n==>返回的会员卡列表==>{}", list);
        return R.ok("返回会员卡列表").put("data", list);
    }

    /**
     * 跳转会员卡页面
     *
     * @return 会员卡页面
     */
    @GetMapping("/x_member_card.do")
    public String memberCard() {
        return "/member/x_member_card";
    }


    /**
     * 跳转添加会员卡页面
     *
     * @return 会员卡页面
     */
    @GetMapping("/x_member_add_card.do")
    public String memberCardAddCard() {
        return "member/x_member_add_card";
    }


    /**
     * 使用mav跳转会员卡编辑页面
     *
     * @return 会员卡编辑页面
     */
    @GetMapping("/x_member_card_edit.do")
    public ModelAndView memberCardEdit(@RequestParam("id") Integer id) {

        //将根据id查到的会员卡信息装进mv
        MemberCardEntity cardById = memberCardService.getById(id);
        log.debug("后端查询：根据id查会员卡信息：cardById={}", cardById);

        //将根据id在中间表中查到的courseCarry【page230】装进mv：对应的会员卡支持的课程
        List<CourseCardEntity> courseCardEntityList = courseCardService.list(new QueryWrapper<CourseCardEntity>().eq("card_id", id));
        log.debug("后端查询：对应会员卡支持的课程：courseCardEntityList==>{}", courseCardEntityList);

        //将上面查到的课程实体返回它的id集合
        List<Long> courseIds = courseCardEntityList.stream().map(CourseCardEntity::getCourseId).collect(Collectors.toList());
        log.debug("上面查到的课程实体返回它的id集合：courseIds:##-->{}", courseIds);

        ModelMap map = new ModelMap();
        map.addAttribute("cardMsg", cardById);
        map.addAttribute("courseCarry", courseIds);

        ModelAndView mv = new ModelAndView("/member/x_member_card_edit", map);
        log.debug("mv 跳转==> /member/x_member_card_edit，携带数据");
        return mv;
    }

    /**
     * 会员卡表单更新操作
     *
     * @param cardEntity    前端提交的会员卡bean封装；
     * @param courseListStr 单独提交的要修改的支持的课程
     * @return 返回
     */
    @PostMapping("/cardEdit.do")
    @ResponseBody
    @Transactional  //加入事务
    public R cardEdit(@NonNull MemberCardEntity cardEntity, String courseListStr) {


        log.debug("前端传入的cardEntity===>{}", cardEntity);
        cardEntity.setLastModifyTime(LocalDateTime.now()).setVersion(cardEntity.getVersion() + 1);

        boolean isSave = memberCardService.updateById(cardEntity);
        log.debug("更新保存会员卡信息是否成功：isSave:{}", isSave);

        log.debug("单独提交的courseListStr：{}", courseListStr);
        
        /* 
        courseCardEntity表是复合主键，不能简单使用mp进行crud
        复合主键更新  思路：：1.先全删掉再添加（暂时采用这个）
                          2.引入mybatisplus-plus（主键添加注解）
         */

        //todo 下面这一段...优化！！！！
        //数据库中查出对应会员卡支持的课程的
        List<CourseCardEntity> courseCardEntityListByCardId = courseCardService.list(new QueryWrapper<CourseCardEntity>().eq("card_id", cardEntity.getId()));
        List<Long> courseIds = courseCardEntityListByCardId.stream().map(CourseCardEntity::getCourseId).sorted(Long::compareTo).collect(Collectors.toList());

        boolean isNotNull = courseListStr != null;
        boolean isNotEmpty = !courseIds.isEmpty();
        if (isNotNull) {
            log.debug("前端传入要修改的支持的课程列表courseListStr非空：courseListStr==>{}", courseListStr);
            //将要修改的可支持的课程id字符串分离成string数组
            String[] split = courseListStr.split(",");
            //将这个string数组转为long数组（courseId）
            List<Long> courseIdList = Arrays.stream(split).map(Long::valueOf).sorted(Long::compareTo).collect(Collectors.toList());
            //courseId的Long型数组，再将这个数组流式赋值，返回CourseCardEntity的集合
            List<CourseCardEntity> courseCardEntityList = courseIdList.stream().map(item -> {
                return new CourseCardEntity().setCardId(cardEntity.getId()).setCourseId(item);
            }).collect(Collectors.toList());

            //逻辑：删除再按照前端传入的信息重建
            if (isNotEmpty) {
                log.debug("数据库中对应会员卡支持的课程也不会空,即将删除原本的可支持的课程信息...");
                boolean isDeleteAllCourseByCardId = courseCardService.removeById(cardEntity.getId());
                log.debug("删除所有该会员卡支持的课程是否成功：isDelete:{}", isDeleteAllCourseByCardId);
            }
            log.debug("数据库中对应的支持的课程是否为空isNotEmpty==>{}(true表示有值)即将保存前端传入的课程信息...", isNotEmpty);
            boolean isSaveAllCourseByCardId = courseCardService.saveCourseCard(courseCardEntityList);
            log.debug("添加所有该会员卡支持的课程是否成功：isSave:{}", isSaveAllCourseByCardId);
        } else {
            log.debug("前端传入要修改的支持的课程列表courseListStr为空：courseListStr==>{}", courseListStr);
            if (isNotEmpty) {
                log.debug("数据库中对应会员卡支持的课程不是空,逻辑：删除原本的可支持的课程信息...");
                boolean isDeleteAllCourseByCardId = courseCardService.removeById(cardEntity.getId());
                log.debug("删除所有该会员卡支持的课程是否成功：isDelete:{}", isDeleteAllCourseByCardId);
            } else {
                log.debug("前端传入的参数和数据库中信息都是空，不做任何处理");
            }
        }

        log.debug("修改更新支持的课程成功");


        R ok = R.ok("修改成功！");
        log.debug("ok,{}", ok);
        //失败改回来
        return ok;
    }


    /**
     * 根据传入的会员卡id删除
     * @param id 前台传入的会员卡id
     */
    @PostMapping("/deleteOne.do")
    @ResponseBody
    @Transactional  //加入事务
    public void deleteOne(Integer id) {
        //外键约束
        boolean isDeleteCourseCard = courseCardService.removeById(id);
        log.debug("由于外键约束：\n 先通过前端传入id删除课程会员卡中间表信息是否成功：isDeleteCourseCard==>{}", isDeleteCourseCard);
        boolean isDeleteMemberCard = memberCardService.removeById(id);
        log.debug("\n 通过前端传入id删除会员卡是否成功：isDelete：{}", isDeleteMemberCard);
    }


    /**
     * 会员卡添加的异步请求
     *
     * @param memberCardEntity 表单提交的实体类
     * @param courseListStr    表单单独提交的可支持的课程str
     * @return R统一返回
     */
    @PostMapping("/cardAdd.do")
    @ResponseBody
    @Transactional
    //todo 表单提交应该加jsr303 使用@Validation
    public R cardAdd(@Valid MemberCardEntity memberCardEntity, BindingResult bindingResult, String courseListStr) {
        //保存会员卡信息
        log.debug("\n 前端传入的memberCardEntity：{}", memberCardEntity);

        if (bindingResult.hasErrors()) {
            HashMap<String, String> map = new HashMap<>();
            bindingResult.getFieldErrors().forEach(item -> {
                String message = item.getDefaultMessage();
                String field = item.getField();
                map.put(field, message);
            });
            log.debug("\n==>打印jsr303的输出==>{}", map);
            return R.error(400, "非法参数!").put("errorMap", map);
        }


        memberCardEntity.setCreateTime(LocalDateTime.now());
        boolean isMemberCardEntity = memberCardService.save(memberCardEntity);
        log.debug("\n 保存前端提交的会员卡信息是否成功（不包括支持课程信息）：isMemberCardEntity{}", isMemberCardEntity);

        //保存对应会员卡支持课程信息
        if (StringUtils.isNotEmpty(courseListStr)) {
            log.debug("\n 前端传入的courseListStr==>{}", courseListStr);
            //将前端传入的courseListStr流式转成course的id集合
            List<Long> courseIds = Arrays.stream(courseListStr.split(",")).map(Long::valueOf).collect(Collectors.toList());
            log.debug("courseListStr转成List<Long> courseIds==>{}", courseIds);
            List<CourseCardEntity> toSaveCourseCardEntityList = courseIds.stream().map(id -> {
                return new CourseCardEntity().setCardId(memberCardEntity.getId()).setCourseId(id);
            }).collect(Collectors.toList());

            boolean isSaveCourseCard = courseCardService.saveCourseCard(toSaveCourseCardEntityList);
            log.debug("会员卡关联可支持的课程保存是否成功：isSaveCourseCard==>{}", isSaveCourseCard);
            if (isSaveCourseCard) {
                //return R.ok("保存成功");
                return new R().put("data", "保存成功！");
            } else {
                return new R().put("data", "保存失败！！");
            }
        } else {
            return new R().put("data", "会员信息保存成功，关联课程为空");
        }
    }


    /**
     * 根据前台传入的会员id返回所有会员卡信息为suggest提供搜索建议
     *
     * @param memberId 会员id
     * @return 该会员拥有的所有激活的会员卡信息
     */
    @PostMapping("/toSearchByMemberId.do")
    @ResponseBody
    public R toSearchByMemberId(@RequestParam("memberId") Long memberId) {
        //根据会员id查该会员的所有会员卡。实体的卡
        List<MemberBindRecordEntity> list = memberBindRecordService.list(new QueryWrapper<MemberBindRecordEntity>().eq("member_id", memberId).select("id", "card_id"));
        log.debug("\n==>测试list结果==>{}", list);
        List<BindCardInfoVo> bindCardInfoVos = list.stream().map(bind -> {
            MemberCardEntity cardById = memberCardService.getById(bind.getCardId());
            String cardName = cardById.getName();
            BindCardInfoVo vo = new BindCardInfoVo()
                    .setId(bind.getId())
                    .setName(cardName);
            return vo;
        }).collect(Collectors.toList());
        log.debug("\n==>用于suggest提供搜索建议的可用会员卡列表==>{}", bindCardInfoVos);
        return R.ok().put("value", bindCardInfoVos);
    }


    /**
     * 封装返回会员预约课程时的表单上的信息：1.会员卡剩余次数、2.每节课每人消耗次数
     *
     * @param bindCardId     前台传入的会员卡id
     * @param scheduleId 前台传入的排课记录id
     * @return 返回一个封装这些信息的vo
     */
    @PostMapping("/cardTip.do")
    @ResponseBody
    public R cardTip(@RequestParam("cardId") Long bindCardId, @RequestParam("scheduleId") Long scheduleId) {
        log.debug("\n==>前台传入的绑定id是==>{}", bindCardId);
        MemberBindRecordEntity bindEntityById = memberBindRecordService.getById(bindCardId);
        ScheduleRecordEntity scheduleWithCourseId = scheduleRecordService.getOne(new QueryWrapper<ScheduleRecordEntity>().eq("id", scheduleId).select("course_id"));

        Long courseId = scheduleWithCourseId.getCourseId();
        CourseEntity courseEntity = courseService.getOne(new QueryWrapper<CourseEntity>().eq("id", courseId).select("times_cost"));

        ReserveFormCountVo vo = new ReserveFormCountVo()
                .setCardId(bindEntityById.getId())
                .setCardTotalCount(bindEntityById.getValidCount())
                .setCardTotalDay(bindEntityById.getValidDay())
                .setCourseId(courseId)
                .setCourseTimesCost(courseEntity.getTimesCost());
        log.debug("\n==>后台封装后返回前台的预约表单上的提示信息：vo==>{}", vo);

        return R.ok().put("data", vo);


    }

    //todo ！！

    /**
     * 会员详情页的激活状态的改变
     *
     * @param memberId   会员id
     * @param bindCardId 绑定记录id
     * @param status     要更新的状态
     * @return r
     */
    @PostMapping("/activeOpt.do")
    @ResponseBody
    public R activeOpt(@RequestParam("memberId") Long memberId,
                       @RequestParam("bindId") Long bindCardId,
                       @RequestParam("status") Integer status) {

        log.debug("\n==>前台传入的信息会员id：{}     会员绑定id:{}，    要修改的status==>{}", memberId, bindCardId, status);
        int result = memberBindRecordService.updateStatus(bindCardId, status);
        if (result > 0) {
            List<CardInfoVo> cardInfoVos = memberBindRecordService.getCardInfo(memberId);
            log.debug("\n==>更新status后返回的会员卡信息==>{}", cardInfoVos);
            MemberBindRecordEntity bindRecordById = memberBindRecordService.getById(bindCardId);
            return new R().put("data", bindRecordById);
        } else {
            return R.error("更新失败");
        }
    }


    /**
     * 会员详情页的充值
     *
     * @param entity   充值表单提交的在充值记录表中存在的该属性的数据
     * @param memberId 表单提交的充值 记录表没有的属性
     * @param bindId   表单提交的会员绑定id【绑定id即为实际的会员卡id】
     * @return r-> 充值是否成功
     */
    @PostMapping("/rechargeOpt.do")
    @ResponseBody
    @Transactional
    public R rechargeOpt(RechargeRecordEntity entity,
                         @RequestParam("memberId") Long memberId,
                         @RequestParam("cardId") Long bindId) {
        log.debug("\n==>前端传入的会员id和==>{}\n会员绑定id是==>{}\n前端传入的充值信息封装为==>{}", memberId, bindId, entity);
        //根据前端传入的会员id和会员卡id查询绑定信息
        entity.setMemberBindId(bindId).setCreateTime(LocalDateTime.now());

        //1.添加充值记录
        boolean isSaveRecharge = rechargeRecordService.save(entity);
        log.debug("\n==>充值1:添加充值记录是否成功==>{}", isSaveRecharge);
        if (!isSaveRecharge) {
            return R.error("添加充值记录失败");
        }
        //2.添加操作记录日志
        MemberLogEntity operateLog = new MemberLogEntity().setType(OperateType.RECHARGE_OPERATION.getMsg())
                .setInvolveMoney(entity.getReceivedMoney())
                .setOperator(entity.getOperator())
                .setMemberBindId(bindId)
                .setCreateTime(LocalDateTime.now())
                .setCardCountChange(entity.getAddCount())
                .setCardDayChange(entity.getAddDay())
                .setNote(entity.getNote());
        boolean isSaveMemberLog = memberLogService.save(operateLog);
        log.debug("\n==>充值2：添加操作记录日志是否成功==>{}", isSaveMemberLog);
        if (!isSaveMemberLog) {
            return R.error("添加操作记录日志失败");
        }

        //3.修改会员卡（绑定表，会员和会员卡关联表才是真正的会员卡，会员卡表只是个模板作用）
        MemberBindRecordEntity bind = memberBindRecordService.getById(bindId);
        bind.setId(bindId)
                .setValidCount(bind.getValidCount() + entity.getAddCount())
                .setValidDay(bind.getValidDay() + entity.getAddDay())
                .setPayMode(entity.getPayMode())
                .setReceivedMoney(bind.getReceivedMoney().add(entity.getReceivedMoney()))
                .setLastModifyTime(LocalDateTime.now())
                .setValidCount(bind.getVersion() + 1);
        boolean isUpdateBinding = memberBindRecordService.updateById(bind);
        log.debug("\n==>充值3：更新会员绑定表是否成功==>{}", isUpdateBinding);
        if (!isUpdateBinding) {
            return R.error("更新绑定表失败");
        }

        return R.ok("充值成功！");
    }


    /**
     * 会员详情页扣费操作
     * @param entity 扣费表单提交的信息
     * @param memberId 会员id
     * @param bindId 会员卡实体信息
     * @return r -> 扣费结果
     */
    @PostMapping("/consumeOpt.do")
    @ResponseBody
    @Transactional
    public R consumeOpt(ConsumeRecordEntity entity,
                        @RequestParam("memberId") Long memberId,
                        @RequestParam("cardId") Long bindId) {
        MemberBindRecordEntity bindById = memberBindRecordService.getById(bindId);
        if (bindById.getValidCount() < entity.getCardCountChange()) {
            return R.error("剩余次数不足,请充值后扣费");
        }

        entity.setMemberBindId(bindId).setOperateType(OperateType.CLASS_DEDUCTION.getMsg()).setCreateTime(LocalDateTime.now());
        //扣费1、添加消费记录
        boolean isSaveConsumeRecord = consumeRecordService.save(entity);
        log.debug("\n==>添加消费记录是否成功==>{}", isSaveConsumeRecord);
        if (!isSaveConsumeRecord) {
            return R.error("添加消费记录失败!");
        }
        //扣费2、添加操作记录
        MemberLogEntity logEntity = new MemberLogEntity()
                .setType(OperateType.CLASS_DEDUCTION.getMsg())
                .setOperator(entity.getOperator())
                .setMemberBindId(bindId)
                .setCreateTime(LocalDateTime.now())
                .setCardCountChange(entity.getCardCountChange())
                .setCardDayChange(entity.getCardDayChange())
                .setNote(entity.getNote());
        boolean isSaveLog = memberLogService.save(logEntity);
        log.debug("\n==>添加操作记录日志是否成功==>{}", isSaveLog);
        if (!isSaveLog) {
            return R.error("添加操作记录日志失败！");
        }
        //扣费3、更新实体会员卡的信息
        bindById.setValidCount(bindById.getValidCount() - entity.getCardCountChange())
                .setValidDay(bindById.getValidDay() - entity.getCardDayChange())
                .setLastModifyTime(LocalDateTime.now())
                .setVersion(bindById.getVersion() + 1);
        boolean isUpdateBindRecord = memberBindRecordService.updateById(bindById);
        if (!isUpdateBindRecord) {
            return R.error("更新会员卡信息失败");
        }

        return R.ok("扣费成功!");
    }


    /**
     * 返回操作记录//todo 还有问题,关联的表有误
     *
     * @param memberId 前台提交的会员id
     * @param bindId   实际存在的会员卡的id
     * @return r -> 操作记录
     */
    @PostMapping("/operateRecord.do")
    @ResponseBody
    public R operateRecord(@RequestParam("memberId") Long memberId,
                           @RequestParam("cardId") Long bindId) {
        List<OperateRecordVo> operateRecordVos = rechargeRecordService.getOperateRecord(memberId, bindId);
        for (OperateRecordVo vo : operateRecordVos) {
            vo.setEndToDate(vo.getLastModifyTime() == null ? vo.getCreateTime() : vo.getLastModifyTime());
        }

        return new R().put("data", operateRecordVos);
    }

}
