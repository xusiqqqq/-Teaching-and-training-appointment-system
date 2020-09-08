/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/******************
 * @Author yejf
 * @Version v1.0
 * @Create 2020-09-04 9:10
 * @Description TODO
 */
@Controller

public class EmployeeController {

	@RequestMapping("/index")
	@ResponseBody
	public String index() {
		return "hello springboot";
	}
	
	@RequestMapping("/toLogin")
	public String toLogin() {
		return "x_login";
	}
	
}
