package com.kclm.xsap.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BindCardCountsVo {
    //会员卡名
    private String name;
    //该卡绑定的数量
    private Integer value;
}
