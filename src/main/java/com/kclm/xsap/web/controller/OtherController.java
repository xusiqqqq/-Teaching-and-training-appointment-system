package com.kclm.xsap.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author fangkai
 * @description
 * @create 2021-12-15 13:56
 */

@Controller
@Slf4j
public class OtherController {


    /**
     * 跳转到微信设置页面
     * @return
     */
    @GetMapping("/wechat/x_wechat.do")
    public String togoWechat() {
        return "x_wechat";
    }

    @GetMapping("/app/x_app_store.do")
    public String togoAppStore() {
        return "x_app_store";
    }

}
