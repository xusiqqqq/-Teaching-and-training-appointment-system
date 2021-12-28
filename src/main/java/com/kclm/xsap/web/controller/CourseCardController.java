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

import com.kclm.xsap.entity.CourseCardEntity;
import com.kclm.xsap.service.CourseCardService;
import com.kclm.xsap.utils.R;


/**
 * 中间表：课程-会员卡
 *
 * @author fangkai
 * @email fk_qing@163.com
 * @date 2021-12-04 16:18:21
 */
@RestController
@RequestMapping("xsap/coursecard")
public class CourseCardController {
    @Autowired
    private CourseCardService courseCardService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = courseCardService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{cardId}")
    public R info(@PathVariable("cardId") Long cardId){
		CourseCardEntity courseCard = courseCardService.getById(cardId);

        return R.ok().put("courseCard", courseCard);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CourseCardEntity courseCard){
		courseCardService.save(courseCard);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CourseCardEntity courseCard){
		courseCardService.updateById(courseCard);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] cardIds){
		courseCardService.removeByIds(Arrays.asList(cardIds));

        return R.ok();
    }

}
