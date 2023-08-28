package com.kclm.xsap.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class LogInfoVo {
    private Long id;

    //操作事件
    private LocalDateTime operateTime;

    //操作的类型
    private String operateType;

    //每次操作次数变化
    private Integer changeCount;

    //每次影响的钱数
    private Integer changeMoney;

    //操作员
    private String operator;

    //操作的备注
    private String cardNote;
    /**
     * 单次操作时卡的状态，默认是1，表示已激活
     */
    private Integer status;


}
