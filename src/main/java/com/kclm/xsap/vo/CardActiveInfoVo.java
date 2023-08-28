package com.kclm.xsap.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CardActiveInfoVo {
    private Long bindId;
    private Long memberId;
    private Integer status;
}
