package com.kclm.xsap.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/******************
 * @Author yejf
 * @Description 认证过滤器【登录过滤器】
 */
@Order(1)
//@ServletComponentScan
@WebFilter(urlPatterns = {"*.do","/index.html","/index"},filterName = "loginFilter", dispatcherTypes =
        {DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC})
public class LoginFilter implements Filter {

    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }
}
