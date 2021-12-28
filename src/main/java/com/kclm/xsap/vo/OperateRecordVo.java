package com.kclm.xsap.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author fangkai
 * @description
 * @create 2021-12-20 17:32
 */

@Data
@Accessors
public class OperateRecordVo {
    /*
    "operateTime"}
"operateType"}
"validTimes"},
"endToDate"},
"involveMoney"
"operator"},
"cardNote"},
": "status",
     */
    /**
     * 操作记录id
     */
    private Long id;
    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime operateTime;
    /**
     * 操作类型
     */
    private String operateType;
    /**
     * 可用次数
     */
    private Integer validTimes;
    /**
     * 卡到期日
     */
    private LocalDateTime endToDate;
    /**
     * 金额
     */
    private BigDecimal involveMoney;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 备注
     */
    private String cardNote;
    /**
     * 状态
     */
    private Integer status;

    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastModifyTime;
}
