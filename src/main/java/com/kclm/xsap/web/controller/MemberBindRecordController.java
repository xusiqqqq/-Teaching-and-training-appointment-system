package com.kclm.xsap.web.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kclm.xsap.consts.OperateType;
import com.kclm.xsap.entity.*;
import com.kclm.xsap.service.*;
import com.kclm.xsap.utils.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.kclm.xsap.utils.R;

import javax.validation.Valid;


/**
 * 中间表：会员绑定记录
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@Slf4j
@Controller
@RequestMapping("/cardBind")
public class MemberBindRecordController {
    @Autowired
    private MemberBindRecordService memberBindRecordService;

    @Autowired
    private ConsumeRecordService consumeRecordService;

    @Autowired
    private MemberLogService memberLogService;

    @Autowired
    private MemberCardService memberCardService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    /**
     * 跳转会员卡绑定页面
     * @return x_member_card_bind.html
     */
    @GetMapping("/x_member_card_bind.do")
    public String memberCardBind() {
        return "/member/x_member_card_bind";
    }

    /**
     * 会员绑卡
     * @param entity 绑卡表单提交的信息
     * @param bindingResult jsr303
     * @param operator 操作人
     * @return r -> 绑卡是否成功
     */
    @PostMapping("/memberBind.do")
    @ResponseBody
    @Transactional
    public R memberBind(@Valid MemberBindRecordEntity entity,
                        BindingResult bindingResult,
                        @RequestParam("operator")String operator) {
        log.debug("\n==>打印从添加会员页面开始的绑定会员卡表单传入的信息==>{}", entity);
        if (bindingResult.hasErrors()) {
            HashMap<String, String> map = new HashMap<>();
            bindingResult.getFieldErrors().forEach(item -> {
                String message = item.getDefaultMessage();
                String field = item.getField();
                map.put(field, message);
            });
            return R.error(400, "非法参数").put("errorMap", map);
        }

        MemberBindRecordEntity isExist = memberBindRecordService.getOne(new QueryWrapper<MemberBindRecordEntity>()
                .eq("member_id", entity.getMemberId())
                .eq("card_id", entity.getCardId()));
        log.debug("\n==>查看数据库中有没有绑定这张卡==>{}", isExist);
        //判断是否重复绑卡
        if (null != isExist && null != isExist.getId()) {
            log.debug("\n==>重复绑卡！");
            return R.error("您已经绑定过此卡！无需绑定");
        }

        //到这里的绑卡操作一定是有数据的；否则会被jsr303返回
        //根据会员卡id查询会员卡自带的次数和有效期
        MemberCardEntity cardEntity = memberCardService.getById(entity.getCardId());

        log.debug("\n==>打印加上卡默认信息之前的entity==>{}", entity);
        //会员绑定操作
        MemberBindRecordEntity bindResult = new MemberBindRecordEntity();
        BeanUtils.copyProperties(entity, bindResult);
        //此处的实收金额是最终的金额，办卡卡本身的费用已经被扣除，所以无须添加办卡费用
        bindResult.setValidCount(entity.getValidCount() + cardEntity.getTotalCount())//充值次数＋卡自带的次数
                .setValidDay(entity.getValidDay() + cardEntity.getTotalDay())   //充值天数+自带天数
                .setReceivedMoney(entity.getReceivedMoney())    //此处金额是充值金额，办卡费用会在消费记录中扣除
                .setPayMode(entity.getPayMode())
                .setNote(entity.getNote())
                .setCreateTime(LocalDateTime.now());

        log.debug("\n==>打印加上卡默认信息之后的entity==>{}", bindResult);
        boolean isSaveBind = memberBindRecordService.save(bindResult);

        log.debug("\n==>绑卡数据库操作是否成功==>{}", isSaveBind);

        if (isSaveBind) {

            //1.1添加操作记录【添加绑定操作】
            MemberLogEntity memberLogEntityForBind = new MemberLogEntity()
                    .setType(OperateType.BINDING_CARD_OPERATION.getMsg())
                    .setInvolveMoney(cardEntity.getPrice())
                    .setOperator(operator)
                    .setMemberBindId(bindResult.getId())
                    .setCreateTime(LocalDateTime.now());

            boolean isSaveLogForBinding = memberLogService.save(memberLogEntityForBind);
            log.debug("\n==>添加会员绑定的操作记录是否成功==>{}", isSaveLogForBinding);
            if (!isSaveLogForBinding) {
                log.debug("添加会员绑定的操作记录失败！");
                return R.error("会员绑定失败！" );
            }
            //1.2添加绑卡原始的充值记录//todo 这里是否合理？绑卡要算在充值吗？//这里将绑卡时卡自带的次数和有效期页视为充值操作
            RechargeRecordEntity rechargeRecordEntity = new RechargeRecordEntity()
                    .setAddCount(cardEntity.getTotalCount())
                    .setAddDay(cardEntity.getTotalDay())
                    .setReceivedMoney(cardEntity.getPrice())
                    .setPayMode(entity.getPayMode())
                    .setOperator(operator)
                    .setNote(cardEntity.getNote())
                    .setMemberBindId(bindResult.getId())
                    .setCreateTime(LocalDateTime.now())
                    .setLogId(memberLogEntityForBind.getId());
            boolean isSaveRechargeWithCard = rechargeRecordService.save(rechargeRecordEntity);
            log.debug("\n==>添加会员绑定的充值记录【初始化卡的充值】即将卡自带的信息记录到充值记录上==>{}", isSaveRechargeWithCard);
            if (!isSaveRechargeWithCard) {
                log.debug("添加会员绑定的充值记录【即初始化卡的充值】失败");
                return R.error("会员绑定失败！");
            }
            //1.3添加绑卡【办卡费用】的消费记录(由于此处的消费是用于扣除办卡的费用，故)
            ConsumeRecordEntity consumeRecordEntity = new ConsumeRecordEntity()
                    .setOperateType(OperateType.BINDING_CARD_OPERATION.getMsg())
                    .setMoneyCost(cardEntity.getPrice())
                    .setOperator(operator)
                    .setNote("办卡的费用")
                    .setMemberBindId(bindResult.getId())
                    .setCreateTime(LocalDateTime.now());
            boolean isSaveConsume = consumeRecordService.save(consumeRecordEntity);
            log.debug("\n==>添加办卡费用扣除是否成功==>{}", isSaveConsume);
            if (!isSaveConsume) {
                log.debug("添加办卡费用失败！");
                return R.error("绑卡失败");
            }

            //2.1添加操作记录【添加绑定时的充值操作】
            MemberLogEntity memberLogEntityForRecharge = new MemberLogEntity()
                    .setType(OperateType.BIND_CARD_RECHARGE_OPERATION.getMsg())
                    .setInvolveMoney(entity.getReceivedMoney())
                    .setOperator(operator)
                    .setMemberBindId(bindResult.getId())
                    .setCreateTime(LocalDateTime.now());
            boolean isSaveLogForRecharge = memberLogService.save(memberLogEntityForRecharge);
            log.debug("\n==>添加会员绑定时的充值操作的操作记录是否成功==>{}", isSaveLogForRecharge);
            if (!isSaveLogForRecharge) {
                log.debug("添加会员绑的充值操作的操作记录失败");
                return R.error("会员绑卡失败！");
            }
            //2.2添加绑卡时的充值记录
            RechargeRecordEntity recordEntityForBinding = new RechargeRecordEntity()
                    .setAddCount(entity.getValidCount())
                    .setAddDay(entity.getValidDay())
                    .setReceivedMoney(entity.getReceivedMoney())
                    .setPayMode(entity.getPayMode())
                    .setOperator(operator)
                    .setNote(entity.getNote())
                    .setMemberBindId(bindResult.getId())
                    .setCreateTime(LocalDateTime.now())
                    .setLogId(memberLogEntityForRecharge.getId());
            boolean isSaveForBinding = rechargeRecordService.save(recordEntityForBinding);
            log.debug("\n==>添加会员绑定时的充值记录是否成功==>{}", isSaveForBinding);
            if(!isSaveForBinding) {
                log.debug("添加会员绑定时的充值记录失败！");
                return R.error("会员绑卡失败");
            }

            return R.ok("会员绑定成功");
        }
        return R.error("会员绑定失败！");
    }




}
