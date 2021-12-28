package com.kclm.xsap.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author fangkai
 * @description
 * @create 2021-12-06 13:05
 */

@Data
@Accessors(chain = true)
public class MemberVo {
    private Long id;
    private String memberName;
    private String gender;
    private String[] cardHold;
//    private String[] clubCard;
//    private String remark;
}
