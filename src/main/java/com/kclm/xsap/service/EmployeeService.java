package com.kclm.xsap.service;

import com.kclm.xsap.entity.TEmployee;

/**
 * 
 * @author harima
 * @since JDK11.0
 * @CreateDate 2020年9月15日 上午10:21:35 
 * @description 此类用来描述了员工相关的业务
 *
 */
public interface EmployeeService {

	/**
	 *  账户登录
	 * @param username 操作员用户名
	 * @param pwd 操作员密码
	 * @return TEmployee。员工信息
	 */
	TEmployee login(String username,String pwd);
	
	/**
	 *  账户注册
	 * @param emp 员工实体类
	 * @return boolean。true：注册成功；false：注册失败
	 */
	boolean register(TEmployee emp);
	
	/**
	 *  忘记密码。通过手机号找出密码，发送短信验证码的方式
	 * @param phone 手机号
	 * @return boolean。true：发送验证码成功；false：发送验证码失败
	 */
	boolean resetPwdByPhone(String phone);
	
	/**
	 *  通过邮箱重置密码
	 * @param email	邮箱
	 * @param content	发送的内容
	 * @param isHtml	content部分是否表现为html格式
	 * @return	boolean。true：发送邮件成功；false：发送邮件失败
	 */
	boolean resetPwdByEmail(String email,String content,Boolean isHtml);
	
	/**
	 *  更新密码
	 * @param username 操作员用户名
	 * @param oldPwd 旧密码
	 * @param newPwd 新密码
	 * @return boolean。true：更新密码成功；false：更新密码失败
	 */
	boolean updatePassword(String username,String oldPwd,String newPwd);
	
	/**
	 *  更新个人信息。包含头像，不含密码
	 * @param emp 员工实体类
	 * @return TEmployee。员工信息
	 */
	TEmployee update(TEmployee emp);
	
}
