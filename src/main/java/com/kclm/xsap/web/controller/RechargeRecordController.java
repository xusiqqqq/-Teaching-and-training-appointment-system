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

import com.kclm.xsap.entity.RechargeRecordEntity;
import com.kclm.xsap.service.RechargeRecordService;
import com.kclm.xsap.utils.R;


/**
 * 充值记录
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:20
 */
@RestController
@RequestMapping("xsap/rechargerecord")
public class RechargeRecordController {
    @Autowired
    private RechargeRecordService rechargeRecordService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = rechargeRecordService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		RechargeRecordEntity rechargeRecord = rechargeRecordService.getById(id);

        return R.ok().put("rechargeRecord", rechargeRecord);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody RechargeRecordEntity rechargeRecord){
		rechargeRecordService.save(rechargeRecord);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody RechargeRecordEntity rechargeRecord){
		rechargeRecordService.updateById(rechargeRecord);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		rechargeRecordService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
