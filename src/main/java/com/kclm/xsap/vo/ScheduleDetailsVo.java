package com.kclm.xsap.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

/**
 * @author fangkai
 * @description
 * @create 2021-12-13 14:34
 */


@Data
@Accessors(chain = true)
public class ScheduleDetailsVo {

    private String courseName;

    private String startTime;

    private String endTime;

    private Long duration;
    /**
     * 限制性别
     */
    private String limitSex;
    /**
     * 限制年龄
     */
    private Integer limitAge;

    private List<String> supportCards;

    private String TeacherName;
    /**
     * 此项排课的预约人数
     */
    private Integer orderNums;

    //classNumbers??
    private Integer classNumbers;

    /**
     * 每节课程需花费的次数
     */
    private Integer timesCost;
}
