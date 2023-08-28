package com.kclm.xsap.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanValidationUtils {

    private BeanValidationUtils() {
    }

    public static R getValidateR(BindingResult bindingResult) {
        Map<String,String> map=new HashMap<>();
        List<FieldError> fieldErrors= bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors)
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
        return  R.error(400,"验证信息错误").put("errorMap",map);
    }
}
