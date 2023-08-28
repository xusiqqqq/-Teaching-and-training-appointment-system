package com.kclm.xsap.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TeacherClassCostVo {
    private Long id;
    private String name;
    private Integer countChange;
    private Float moneyCost;
}
