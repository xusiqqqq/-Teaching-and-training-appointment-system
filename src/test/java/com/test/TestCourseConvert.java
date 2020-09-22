/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.test;

import com.kclm.xsap.XsapApplication;
import com.kclm.xsap.entity.TCourse;
import com.kclm.xsap.targetdto.CourseConvert;
import com.kclm.xsap.targetdto.CourseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/******************
 * @Author yejf
 * @Version v1.0
 * @Create 2020-09-16 14:24
 * @Description TODO
 */
@SpringBootTest(classes = XsapApplication.class)
public class TestCourseConvert {

	
    @Test
    public void testConvert() {
        //create Entity
        TCourse entity = new TCourse();
        entity.setName("钢琴一对一");
        entity.setDuration(45);
        entity.setColor("#ddee45");
        entity.setContains(1);
        entity.setIntroduce("一对一教学");
        entity.setTimesCost(1);
        entity.setLimitAge(-1);
        entity.setLimitSex("全部");
        entity.setLimitCounts(3);
        //支持的会员卡
        //..
        System.out.println(entity);

        //
        //final CourseConvert mapper = Mappers.getMapper(CourseConvert.class);
        //final CourseDTO courseDTO = mapper.entity2Dto(entity);
        final CourseDTO courseDTO = CourseConvert.INSTANCE.entity2Dto(entity);
        //
        System.out.println(courseDTO);

        //
        courseDTO.setCourseName("钢琴一对多");
        //
        System.out.println(courseDTO);

        //
        final TCourse tCourse = CourseConvert.INSTANCE.dto2Entity(courseDTO);
        //
        System.out.println(tCourse);
    }

}
