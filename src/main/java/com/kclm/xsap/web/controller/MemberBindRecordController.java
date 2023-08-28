package com.kclm.xsap.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.MemberBindRecordService;
import com.kclm.xsap.service.MemberCardService;
import com.kclm.xsap.service.MemberLogService;
import com.kclm.xsap.service.RechargeRecordService;
import com.kclm.xsap.utils.BeanValidationUtils;
import com.kclm.xsap.utils.R;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;


@Controller
@RequestMapping("/cardBind")
public class MemberBindRecordController {
    @Resource
    private MemberBindRecordService bindRecordService;

    @Resource
    private MemberCardService cardService;

    @Resource
    private MemberLogService logService;

    @Resource
    private RechargeRecordService rechargeService;

    /**
     * 为新会员绑卡，并将该操作存入操作日志,并添加RechargeRecord
     * @param bindRecord
     * @param bindingResult
     * @param request
     * @return
     */
    @Transactional
    @ResponseBody
    @PostMapping("/memberBind.do")
    public R togoCardBind(@Valid MemberBindRecordEntity bindRecord, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) return BeanValidationUtils.getValidateR(bindingResult);
        LambdaQueryWrapper<MemberBindRecordEntity> qw=new LambdaQueryWrapper<>();
        qw.eq(MemberBindRecordEntity::getMemberId,bindRecord.getMemberId())
                .eq(MemberBindRecordEntity::getCardId,bindRecord.getCardId());
        if(bindRecordService.count(qw)>0) return R.error("该用户已经绑定过这张卡了，请勿重复绑卡");

        LocalDateTime currentTime = LocalDateTime.now();
        EmployeeEntity operator = (EmployeeEntity) request.getSession().getAttribute("LOGIN_USER");

        MemberCardEntity card = cardService.getById(bindRecord.getCardId());
        bindRecord.setValidCount(bindRecord.getValidCount() + card.getTotalCount());
        bindRecord.setValidDay(bindRecord.getValidDay() + card.getTotalDay());
        bindRecord.setActiveStatus(1);
        bindRecord.setCreateTime(currentTime);
        bindRecord.setLastModifyTime(currentTime);
        boolean b1 = bindRecordService.save(bindRecord);

        MemberLogEntity log = new MemberLogEntity();
        log.setOperator(operator.getName());
        log.setInvolveMoney(bindRecord.getReceivedMoney());
        log.setMemberBindId(bindRecord.getId());
        log.setCreateTime(currentTime);
        log.setCardCountChange(bindRecord.getValidCount());
        log.setCardActiveStatus(bindRecord.getActiveStatus());
        log.setType("绑卡充值操作");
        boolean b2 = logService.save(log);

        RechargeRecordEntity rechargeRecord = new RechargeRecordEntity();
        rechargeRecord.setAddCount(bindRecord.getValidCount());
        rechargeRecord.setAddDay(bindRecord.getValidDay());
        rechargeRecord.setReceivedMoney(bindRecord.getReceivedMoney());
        rechargeRecord.setPayMode(bindRecord.getPayMode());
        rechargeRecord.setOperator(operator.getName());
        rechargeRecord.setNote(bindRecord.getNote());
        rechargeRecord.setMemberBindId(bindRecord.getId());
        rechargeRecord.setCreateTime(currentTime);
        rechargeRecord.setLogId(log.getId());
        boolean b3 = rechargeService.save(rechargeRecord);
        if (b1 && b2 && b3) {
            return R.ok("绑卡成功");
        } else {
            return R.error("未知错误，绑卡失败，请联系系统管理员");
        }

    }

    /**
     * 为旧会员或者未绑卡的新会员绑卡
     * @return
     */
    @GetMapping("/x_member_card_bind.do")
    public String togoMemberCardBind(){
        return "/member/x_member_card_bind";
    }
}
