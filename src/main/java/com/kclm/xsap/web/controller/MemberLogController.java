package com.kclm.xsap.web.controller;

import java.util.Arrays;
import java.util.Map;

import com.kclm.xsap.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kclm.xsap.entity.MemberLogEntity;
import com.kclm.xsap.service.MemberLogService;
import com.kclm.xsap.utils.R;


/**
 * 操作记录
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@RestController
@RequestMapping("xsap/memberlog")
public class MemberLogController {
    @Autowired
    private MemberLogService memberLogService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberLogService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberLogEntity memberLog = memberLogService.getById(id);

        return R.ok().put("memberLog", memberLog);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberLogEntity memberLog){
		memberLogService.save(memberLog);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberLogEntity memberLog){
		memberLogService.updateById(memberLog);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberLogService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
