package com.kclm.xsap.web.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kclm.xsap.entity.TMember;
import com.kclm.xsap.entity.TMemberBindRecord;
import com.kclm.xsap.service.MemberCardService;
import com.kclm.xsap.service.MemberService;

@Controller
@RequestMapping("/cardBind")
public class CardBindController {

	//文件夹名
	private static final String folderName = "member/";
	
	@Autowired
	MemberCardService cardService;
	
	@Autowired
	MemberService memberService;
	
	/* =========会员 - 会员卡绑定操作=========== */
	/*=======页面跳转 */
	//=》绑卡页面
	@RequestMapping("/x_member_card_bind.do")
	public String x_member_card_bind() {
		return folderName + "x_member_card_bind";
	}
	
	//=》批量绑卡
	@RequestMapping("/x_member_bind.do")
	public String x_member_bind() {
		return folderName + "x_member_bind";
	}
	/*=========业务操作 */
	
	//当前会员绑卡
	@ResponseBody
	@RequestMapping("/memberBind.do")
	public TMemberBindRecord memberBind(TMemberBindRecord bind) {
		//检测用。version 4：绑卡成功；
		TMemberBindRecord checkBind = new TMemberBindRecord();
		
		TMemberBindRecord bindRecord = cardService.findBindRecord(bind.getMemberId(), bind.getCardId());
		
		//未选中任何卡
		if(bind.getCardId() == null) {
			checkBind.setNote("未选中任何卡，请选择！");
			return checkBind;
		}
		
		//绑卡时，未缴费
		if(bind.getReceivedMoney() == null || bind.getReceivedMoney() == BigDecimal.ZERO) {
			checkBind.setNote("绑卡时，未缴费");
			return checkBind;
		}
		
		//未选择支付方式
		if(bind.getPayMode() == null || bind.getPayMode().length() < 1) {
			checkBind.setNote("未选择支付方式");
			return checkBind;
		}
		
		//当前会员已经绑过此卡
		if(bindRecord != null) {
			checkBind.setNote("已绑定过此卡，请更换！");
			return checkBind;
		}
		memberService.bindCard(bind);
		checkBind.setVersion(4);
		return checkBind;
	}
	
	//指定会员绑卡
	@ResponseBody
	@RequestMapping("/memberBindCard.do")
	public TMemberBindRecord memberBindCard(TMemberBindRecord bind) {
		//检测用。version 4：绑卡成功；
		TMemberBindRecord checkBind = new TMemberBindRecord();
		System.out.println("------------");
		System.out.println(bind);
		System.out.println("------------");
		
		TMemberBindRecord bindRecord = cardService.findBindRecord(bind.getMemberId(), bind.getCardId());
		
		//未选中任何卡
		if(bind.getCardId() == null) {
			checkBind.setNote("未选中任何卡，请选择！");
			return checkBind;
		}
		//未选择任何会员
		if(bind.getMemberId() == null) {
			checkBind.setNote("未选中任何会员，请选择！");
			return checkBind;
		}
		
		//绑卡时，未缴费
		if(bind.getReceivedMoney() == null  || bind.getReceivedMoney() == BigDecimal.ZERO) {
			checkBind.setNote("绑卡时，未缴费");
			return checkBind;
		}
		
		//未选择支付方式
		if(bind.getPayMode() == null || bind.getPayMode().length() < 1) {
			checkBind.setNote("未选择支付方式");
			return checkBind;
		}
		
		//当前会员已经绑过此卡
		if(bindRecord != null) {
			checkBind.setNote("已绑定过此卡，请更换！");
			return checkBind;
		}
		
		//会员不存在的异常处理
		TMember member = memberService.getMember(bind.getMemberId());
		if(member != null) {
			memberService.bindCard(bind);
			checkBind.setVersion(4);
		}
		return checkBind;
	}
	
}
