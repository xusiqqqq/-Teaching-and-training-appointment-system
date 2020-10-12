package com.kclm.xsap.dto.convert;

import com.kclm.xsap.dto.convert.demo.UserMapping;
import com.kclm.xsap.dto.demo.UserDTO;
import com.kclm.xsap.entity.demo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class UserMappingTest {

    @Autowired
    private UserMapping userMapping;

    @Test
    void entity2DTO() {
        //
        User user = new User();
        user.setId(100L)
                .setUsername("张三丰")
                .setBirthday(LocalDate.of(1998,12,18))
                .setCreateTime(LocalDateTime.now())
                .setPassword("123456")
                .setSex(1)
                .setTest("测试用户")
                .setConfig("[{\"field1\":\"Test Field1\",\"field2\":500}]");
        //
        System.out.println(user);
        //
        final UserDTO userDTO = userMapping.entity2DTO(user);
        System.out.println(userDTO);
    }

    @Test
    void dto2Entity() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(200L)
                .setUsername("张三丰")
                .setBirthday(LocalDate.of(1998,12,18))
                .setCreateTime("2020-09-18 12:34:55")
                .setPassword("123456")
                .setGender(1)
                .setTest("测试用户");
        UserDTO.UserConfig u1 = new UserDTO.UserConfig();
        u1.setField1("key1").setField2(100);
        UserDTO.UserConfig u2 = new UserDTO.UserConfig();
        u2.setField1("key2").setField2(200);
        //
        List<UserDTO.UserConfig> uc = Arrays.asList(u1,u2);
        userDTO.setConfig(uc);
        //
        System.out.println(userDTO);
        //
        final User user = userMapping.dto2Entity(userDTO);

        System.out.println(user);
    }
}