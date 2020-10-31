package com.kclm.xsap.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/wechat")
public class WeChatController {

	/* ----------------页面跳转---------------- */
	@RequestMapping("/x_wechat.do")
	public String x_wechat() {
		return "x_wechat";
	}
	
}
