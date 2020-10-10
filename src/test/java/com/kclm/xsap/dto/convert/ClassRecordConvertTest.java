package com.kclm.xsap.dto.convert;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class ClassRecordConvertTest {

    @Autowired
    private ClassRecordConvert classRecordConvert;

    @Test
    void entity2Dto() {
        //
        //ClassRecordConvert crc = ClassRecordConvert.INSTANCE;
        //
        System.out.println(classRecordConvert);
    }
}