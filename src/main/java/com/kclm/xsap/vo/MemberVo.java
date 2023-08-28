package com.kclm.xsap.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author fangkai
 * @description
 * @create 2021-12-06 13:05
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MemberVo {
    private Long id;

    private String phone;
    private String memberName;
    private String gender;
    private String[] cardHold;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;
    private String note;

}
