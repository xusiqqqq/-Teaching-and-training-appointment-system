/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/******************
 * @Author yejf
 * @Version v1.0
 * @Create 2020-09-07 18:00
 * @Description 公共的实体类
 */
@Getter
@Setter
public class BaseEntity {

    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date lastModifyTime;

    /**
     * 版本
     */
    private Integer version;

}
