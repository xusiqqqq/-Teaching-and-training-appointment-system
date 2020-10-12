/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.dto.demo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

/******************
 * @Author yejf
 * @Version v1.0
 * @Create 2020-10-10 13:23
 * @Description TODO
 */
@Data
@Accessors(chain = true)
public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private Integer gender;
    private LocalDate birthday;
    private String createTime;
    private List<UserConfig> config;
    private String test; // 测试字段
    @Data
    public static class UserConfig {
        private String field1;
        private Integer field2;
    }
}
