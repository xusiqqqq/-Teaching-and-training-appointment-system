/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.dto.convert.demo;

import cn.hutool.json.JSONUtil;
import com.kclm.xsap.dto.demo.UserDTO;
import com.kclm.xsap.entity.demo.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/******************
 * @Author yejf
 * @Version v1.0
 * @Create 2020-10-10 13:25
 * @Description TODO
 */
@Mapper(componentModel = "spring")
public interface UserMapping {

    @Mapping(target = "gender", source = "sex")
    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    UserDTO entity2DTO(User user);

    @Mapping(target = "sex", source = "gender")
    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "password", ignore = true)
    User dto2Entity(UserDTO userDTO);

    //由于 User类中的config是 String类型，而对应的DTO的config是 List<UserConfig> 类型，所以，需要定义映射，我们这里采用默认方法来完成
    default List<UserDTO.UserConfig> strConfigToListUserConfig(String config) {
        System.out.println("---调用了 strConfigToListUserConfig方法...");
        return JSONUtil.parseArray(config).toList(UserDTO.UserConfig.class);
    }

    default String listUserConfigToStrConfig(List<UserDTO.UserConfig> config) {
        //
        System.out.println("**** invoke listUserConfigToStrConfig method() ... ");
        //
        return JSONUtil.toJsonPrettyStr(config);
    }
}
