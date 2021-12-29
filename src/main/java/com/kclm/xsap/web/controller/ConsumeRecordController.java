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

import com.kclm.xsap.entity.ConsumeRecordEntity;
import com.kclm.xsap.service.ConsumeRecordService;
import com.kclm.xsap.utils.R;


/**
 * 消费记录
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
@RestController
@RequestMapping("xsap/consumerecord")
public class ConsumeRecordController {
    @Autowired
    private ConsumeRecordService consumeRecordService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = consumeRecordService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		ConsumeRecordEntity consumeRecord = consumeRecordService.getById(id);

        return R.ok().put("consumeRecord", consumeRecord);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ConsumeRecordEntity consumeRecord){
		consumeRecordService.save(consumeRecord);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody ConsumeRecordEntity consumeRecord){
		consumeRecordService.updateById(consumeRecord);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		consumeRecordService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
