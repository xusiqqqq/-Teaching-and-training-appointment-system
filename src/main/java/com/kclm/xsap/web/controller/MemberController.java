package com.kclm.xsap.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberController {

	//文件夹名
	private static final String folderName = "member";
	
	
	/* ============会员操作========= */
	/* 页面跳转 */
	//=》会员列表
	@RequestMapping("/x_member_list.do")
	public String x_member_list() {
		return folderName + "/x_member_list";
	}
	//=》添加会员
	@RequestMapping("/x_member_add.do")
	public String x_member_add() {
		return folderName + "/x_member_add";
	}
	//=》批量导入会员
	@RequestMapping("/x_member_import.do")
	public String x_member_import() {
		return folderName + "/x_member_import";
	}
	//=》批量绑卡
	@RequestMapping("/x_member_bind.do")
	public String x_member_bind() {
		return folderName + "/x_member_bind";
	}
	
	/* 业务处理 */
	//会员信息概览
	
	
	
	/* ==========会员卡操作=========== */
	@RequestMapping("/x_member_card.do")
	public String x_member_card() {
		return folderName + "/x_member_card";
	}
	
	/* =========会员绑定操作=========== */
	@RequestMapping("/x_member_card_bind.do")
	public String x_member_card_bind() {
		return folderName + "/x_member_card_bind";
	}
	
	
}
