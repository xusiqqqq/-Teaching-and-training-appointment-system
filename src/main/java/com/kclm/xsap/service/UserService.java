package com.kclm.xsap.service;

import com.kclm.xsap.entity.TEmployee;

public interface UserService {

    TEmployee login(String userName, String pwd);

    void register(TEmployee employee);

}
