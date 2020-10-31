package com.kclm.xsap.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app")
public class ApplicationController {

	/* ============用作页面跳转============ */
	@RequestMapping("/x_app_store.do")
	public String x_app_store() {
		return "x_app_store";
	}
	
}
