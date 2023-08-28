package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.BeanValidationUtils;
import com.kclm.xsap.utils.R;
import com.kclm.xsap.vo.ConsumeFormVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/card")
public class MemberCardController {
    @Resource
    private MemberCardService memberCardService;
    @Resource
    private MemberBindRecordService memberBindRecordService;

    @Resource

    private MemberLogService memberLogService;

    @Resource
    private CourseCardService courseCardService;

    @Resource
    private CourseService courseService;

    @Resource
    private ConsumeRecordService consumeService;

    @Resource
    private RechargeRecordService rechargeService;

    @Resource
    private ClassRecordService classRecordService;


    /**
     * 返会会员卡激活状态，并且存储此次操作记录
     * @param request
     * @param memberId
     * @param bindId
     * @param status
     * @return
     */
    @PostMapping("/activeOpt.do")
    @ResponseBody
    @Transactional
    public R isActive(HttpServletRequest request,Long memberId, Long bindId, int status){
        MemberBindRecordEntity bindRecord = memberBindRecordService.getById(bindId);
        bindRecord.setActiveStatus(status);
        memberBindRecordService.updateById(bindRecord);
        MemberLogEntity log=new MemberLogEntity();
        if(status==1){
            log.setType("激活");
            log.setNote("激活会员卡操作");
        }else{
            log.setType("停用");
            log.setNote("停用会员卡操作");
        }
        log.setCardActiveStatus(status);
        log.setMemberBindId(bindId);
        log.setInvolveMoney(BigDecimal.ZERO);
        EmployeeEntity operator = (EmployeeEntity) request.getSession().getAttribute("LOGIN_USER");
        log.setOperator(operator.getName());
        log.setCreateTime(LocalDateTime.now());
        log.setVersion(1);

        memberLogService.save(log);
        return new R().put("data",status);
    }

    /**
     * 跳到会员卡页面
     * @return
     */
    @GetMapping("/x_member_card.do")
    public String togoMemberCard(){
        return "/member/x_member_card";
    }

    /**
     * 返回所有的会员卡信息
     * @return
     */
    @PostMapping("/cardList.do")
    @ResponseBody
    public R getAllCards(){
        return new R().put("data",memberCardService.list());
    }

    /**
     * 添加会员卡，1、存储会员卡信息  2、存储会员卡支持的课程信息
     * @return
     */
    @GetMapping("/x_member_add_card.do")
    public String togoCardAdd(){
        return "member/x_member_add_card";
    }

    /**
     * 会员卡添加
     * @param card
     * @param courseListStr
     * @param bindingResult
     * @return
     */
    @Transactional
    @PostMapping("/cardAdd.do")
    @ResponseBody
    public R addCard(@Valid MemberCardEntity card, Long[] courseListStr, BindingResult bindingResult){
        if(bindingResult.hasErrors()) return BeanValidationUtils.getValidateR(bindingResult);
        card.setCreateTime(LocalDateTime.now());
        boolean b1 = memberCardService.save(card);
        List<CourseCardEntity> list=new ArrayList<>();
        for (Long id : courseListStr) {
            CourseCardEntity courseCard=new CourseCardEntity();
            courseCard.setCardId(card.getId());
            courseCard.setCourseId(id);
            list.add(courseCard);
        }
        boolean b2 = courseCardService.saveBatch(list);
        if(b1&&b2)  return R.ok("存储成功");
        else return R.error("存储失败");
    }

    /**
     * 显示一个会员的一张会员卡的所有操作记录
     * @param memberId
     * @param bindId
     * @return
     */
    @Transactional
    @PostMapping("/operateRecord.do")
    @ResponseBody
    public R addOperateRecord(Long memberId,Long bindId){
        //通过绑卡记录的id，查询该会员的该张卡的全部操作记录
        return new R().put("data",memberCardService.getLogInfoVos(bindId));
    }


    /**
     * 跳转到会员卡编辑页面，回显当前会员卡信息
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/x_member_card_edit.do")
    public String togoCardEdit(Model model, Long id){
        //通过cardId查出卡的信息，然后查出这张卡能上的课程
        MemberCardEntity card = memberCardService.getById(id);
        LambdaQueryWrapper<CourseCardEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(CourseCardEntity::getCardId,id);
        List<CourseCardEntity> courseCardList = courseCardService.list(qw);
        List<Long> courseIds=new ArrayList<>();
        for (CourseCardEntity entity : courseCardList) {
            CourseEntity course = courseService.getById(entity.getCourseId());
            courseIds.add(course.getId());
        }
        model.addAttribute("cardMsg",card);
        model.addAttribute("courseCarry",courseIds);
        return "member/x_member_card_edit";
    }

    /**
     * 会员卡编辑操作
     * @param card
     * @param bindingResult
     * @param courseListStr
     * @return
     */
    @PostMapping("/cardEdit.do")
    @ResponseBody
    @Transactional
    public R updateCard(@Valid MemberCardEntity card,BindingResult bindingResult,Long[] courseListStr){
        if(bindingResult.hasErrors()){
            return BeanValidationUtils.getValidateR(bindingResult);
        }
        //首先，需要更新卡的信息可以根据cardId进行update
        card.setLastModifyTime(LocalDateTime.now());
        boolean b1 = memberCardService.updateById(card);
        //其次，卡和课程绑定的信息，可以通过先删除，然后在添加的方法进行。
        boolean b2 = courseCardService.remove(new LambdaQueryWrapper<CourseCardEntity>().eq(CourseCardEntity::getCardId, card.getId()));
        boolean b3 = memberCardService.saveCardCourse(card, courseListStr);
        if(b1&&b2&&b3) return R.ok("更新成功");
        else  return R.error("更新失败，未知错误");
    }

    /**
     * 搜索该会员名下所有有效的会员卡，需注意，这里应该不显示已经被禁用掉的卡，课程不支持的卡也应该不显示
     * @param memberId
     * @return
     */
    @PostMapping("/toSearchByMemberId.do")
    @ResponseBody
    public R searchByMemberId(Long memberId){
        LambdaQueryWrapper<MemberBindRecordEntity> qw= new LambdaQueryWrapper<>();
        qw.eq(MemberBindRecordEntity::getMemberId, memberId).eq(MemberBindRecordEntity::getActiveStatus,1);
        List<MemberBindRecordEntity> list = memberBindRecordService.list(qw);
        List<Long> cardIds=new ArrayList<>();
        if(!list.isEmpty()) for (MemberBindRecordEntity bindRecord : list) cardIds.add(bindRecord.getCardId());
        List<MemberCardEntity> cards = memberCardService.listByIds(cardIds);
        return R.ok().put("value",cards);
    }

    /**
     * 会员对应的卡的剩余的次数
     * @param cardId
     * @param memberId
     * @param scheduleId
     * @return
     */
    @ResponseBody
    @PostMapping("/cardTip.do")
    public R getCardTip(Long cardId,Long memberId,Long scheduleId){
        LambdaQueryWrapper<MemberBindRecordEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(MemberBindRecordEntity::getCardId,cardId).eq(MemberBindRecordEntity::getMemberId,memberId);
        MemberBindRecordEntity bindRecord = memberBindRecordService.getOne(qw);
        CourseEntity course = courseService.getCourseByScheduleId(scheduleId);
        Map<String,String> map=new HashMap<>();
        map.put("cardTotalCount",bindRecord.getValidCount().toString());
        map.put("courseTimesCost",course.getTimesCost().toString());
        return R.ok().put("data",map);
    }

    /**
     * 充值，需要修改bindRecord表中的数据，需要添加日志，并添加充值记录
     * @param rechargeRecord
     * @param bindingResult
     * @return
     */
    @ResponseBody
    @Transactional
    @PostMapping("/rechargeOpt.do")
    public R rechargeOpt(@Valid RechargeRecordEntity rechargeRecord,BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return BeanValidationUtils.getValidateR(bindingResult);
        }

        MemberBindRecordEntity bindRecord=memberBindRecordService.getById(rechargeRecord.getMemberBindId());
        MemberLogEntity log=new MemberLogEntity();
        LocalDateTime currentTime=LocalDateTime.now();

        bindRecord.setValidCount(bindRecord.getValidCount()+rechargeRecord.getAddCount());
        bindRecord.setValidDay(bindRecord.getValidDay()+rechargeRecord.getAddDay());
        bindRecord.setReceivedMoney(bindRecord.getReceivedMoney().add(rechargeRecord.getReceivedMoney()));
        bindRecord.setLastModifyTime(currentTime);
        boolean b1 = memberBindRecordService.updateById(bindRecord);

        log.setType("充值操作");
        log.setInvolveMoney(rechargeRecord.getReceivedMoney());
        log.setOperator(rechargeRecord.getOperator());
        log.setMemberBindId(rechargeRecord.getMemberBindId());
        log.setCreateTime(currentTime);
        log.setVersion(1);
        log.setCardCountChange(rechargeRecord.getAddCount());
        log.setCardDayChange(rechargeRecord.getAddDay());
        log.setNote(rechargeRecord.getNote());
        log.setCardActiveStatus(bindRecord.getActiveStatus());
        boolean b2=memberLogService.save(log);

        rechargeRecord.setLogId(log.getId());
        rechargeRecord.setCreateTime(currentTime);
        rechargeRecord.setVersion(1);
        boolean b3 = rechargeService.save(rechargeRecord);
        if(b1&&b2&&b3) return R.ok("充值成功");
        else return R.error("充值失败，未知错误");

    }

    /**
     * 单独扣费，需要填写消费记录表，修改bindRecord表中的相应的数据，还要再添加日志。
     *  需要补上上课记录
     * @param vo 扣费信息ConsumeFormVo
     * @return
     */
    @ResponseBody
    @Transactional
    @PostMapping("/consumeOpt.do")
    public R consumeOpt(ConsumeFormVo vo){
        MemberBindRecordEntity bindRecord=memberBindRecordService.getById(vo.getCardBindId());
        MemberLogEntity log=new MemberLogEntity();
        ConsumeRecordEntity consumeRecord=new ConsumeRecordEntity();
        ClassRecordEntity classRecord=new ClassRecordEntity();
        LocalDateTime currentTime=LocalDateTime.now();
        if(bindRecord.getValidCount()<vo.getCardCountChange()){return R.error("该卡次数不足，无法扣除");}
        if(bindRecord.getCreateTime().plusDays(bindRecord.getValidDay()).isBefore(currentTime)){return R.error("该卡已过期");}
        if(bindRecord.getActiveStatus()==0){return R.error("该卡已经被禁用");}
        bindRecord.setValidCount(bindRecord.getValidCount()-vo.getCardCountChange());
        bindRecord.setLastModifyTime(currentTime);
        boolean b1 = memberBindRecordService.updateById(bindRecord);

        log.setType("会员上课补费");
        log.setInvolveMoney(vo.getAmountOfConsumption());
        log.setOperator(vo.getOperator());
        log.setMemberBindId(vo.getCardBindId());
        log.setCreateTime(currentTime);
        log.setVersion(1);
        log.setCardCountChange(vo.getCardCountChange());
        log.setCardDayChange(vo.getCardDayChange());
        log.setNote(vo.getNote());
        log.setCardActiveStatus(bindRecord.getActiveStatus());
        boolean b2= memberLogService.save(log);

        consumeRecord.setOperateType("会员上课补费");
        consumeRecord.setCardCountChange(vo.getCardCountChange());
        consumeRecord.setCardDayChange(vo.getCardDayChange());
        consumeRecord.setMoneyCost(vo.getAmountOfConsumption());
        consumeRecord.setOperator(vo.getOperator());
        consumeRecord.setNote(vo.getMemberId()+"会员扣了"+vo.getAmountOfConsumption());
        consumeRecord.setMemberBindId(vo.getCardBindId());
        consumeRecord.setCreateTime(currentTime);
        consumeRecord.setVersion(1);
        consumeRecord.setLogId(log.getId());
        consumeRecord.setScheduleId(vo.getScheduleId());
        boolean b3 = consumeService.save(consumeRecord);

        classRecord.setMemberId(bindRecord.getMemberId());
        classRecord.setCardName(memberCardService.getById(bindRecord.getCardId()).getName());
        classRecord.setScheduleId(vo.getScheduleId());
        classRecord.setNote("手动添加扣费用户");
        classRecord.setCheckStatus(1);
        classRecord.setReserveCheck(1);
        classRecord.setCreateTime(currentTime);
        classRecord.setBindCardId(bindRecord.getId());
        boolean b4 = classRecordService.save(classRecord);
        if(b1&&b2&&b3&&b4) return R.ok("扣费成功");
        else return R.error("扣费失败，未知错误");
    }

    /**
     * 删除会员卡
     * @param id
     * @return
     */
    @ResponseBody
    @Transactional
    @PostMapping("/deleteOne.do")
    public R deleteCard(Long id){
        //处于上课状态中，无法删除
        //想要删除卡，就需要去禁用所有的 这种卡
        List<MemberBindRecordEntity> bindList = memberBindRecordService.list(new LambdaQueryWrapper<MemberBindRecordEntity>().eq(MemberBindRecordEntity::getCardId,id));
        for (MemberBindRecordEntity bindRecord : bindList) {
            int count = classRecordService.count(new LambdaQueryWrapper<ClassRecordEntity>().eq(ClassRecordEntity::getBindCardId, bindRecord.getId()).eq(ClassRecordEntity::getCheckStatus, 0));
            if(count>0) return R.error("有会员正使用该类型的卡上课，并且还未支付费用");
        }
        List<MemberBindRecordEntity> bindList2 = memberBindRecordService.list(new LambdaQueryWrapper<MemberBindRecordEntity>().eq(MemberBindRecordEntity::getCardId,id).eq(MemberBindRecordEntity::getActiveStatus, 1));
        if(!bindList2.isEmpty()) return R.error("该会员卡目前还有用户在使用，请禁用所有的该卡之后，才能进行删除");
        boolean b = memberCardService.removeById(id);
        courseCardService.remove(new LambdaQueryWrapper<CourseCardEntity>().eq(CourseCardEntity::getCardId,id));
        if(b) return R.ok("删除成功");
        else return R.error("删除失败");
    }

}
