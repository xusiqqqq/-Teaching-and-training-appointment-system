package com.kclm.xsap.entity;

import lombok.Data;

@Data
public class TCourseCard extends BaseEntity {
    /**
    * 会员卡id
    */
    private Long cardId;

    /**
    * 课程id
    */
    private Long cid;
}