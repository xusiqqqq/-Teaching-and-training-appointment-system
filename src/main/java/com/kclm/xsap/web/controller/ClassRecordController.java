package com.kclm.xsap.web.controller;

import java.util.Arrays;
import java.util.Map;

import com.kclm.xsap.utils.PageUtils;
import com.kclm.xsap.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kclm.xsap.entity.ClassRecordEntity;
import com.kclm.xsap.service.ClassRecordService;



/**
 * 
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
@RestController
@RequestMapping("xsap/classrecord")
public class ClassRecordController {
    @Autowired
    private ClassRecordService classRecordService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = classRecordService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		ClassRecordEntity classRecord = classRecordService.getById(id);

        return R.ok().put("classRecord", classRecord);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ClassRecordEntity classRecord){
		classRecordService.save(classRecord);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody ClassRecordEntity classRecord){
		classRecordService.updateById(classRecord);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		classRecordService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
