/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.targetdto;

import lombok.Data;

/******************
 * @Author yejf
 * @Version v1.0
 * @Create 2020-09-16 14:21
 * @Description TODO
 */
@Data
public class CourseDTO {

    private String courseName; //如果列名不一样，可以通过mapping映射

    /**
     * 课程时长，单位：分钟
     */
    private Integer duration;

    /**
     * 上课人数
     */
    private Integer contains;

    /**
     * 卡片颜色
     */
    private String color;

    /**
     * 课程介绍
     */
    private String introduce;

    /**
     * 每节课程需花费的次数
     */
    private Integer timesCost;

    /**
     * 限制性别
     */
    private String limitSex;

    /**
     * 限制年龄
     */
    private Integer limitAge;

    /**
     * 限制预约次数
     */
    private Integer limitCounts;

}
