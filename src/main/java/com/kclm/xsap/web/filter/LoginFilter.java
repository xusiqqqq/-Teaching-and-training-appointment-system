package com.kclm.xsap.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

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
@WebFilter(urlPatterns = {"/function/*"},filterName = "loginFilter", dispatcherTypes =
        {DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC})
public class LoginFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.debug("-- LoginFilter的init()方法....");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //类型强制转换
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        //获取 HttpSession
        HttpSession session = req.getSession(false);
        if (session == null) {
            //肯定没有登录
            LOGGER.debug("此客户端肯定没有登录过，因为session为null");
            resp.sendRedirect(req.getServletContext().getContextPath() + "/user/toLogin");
            //req.getRequestDispatcher("/user/toLogin").forward(req, resp);
        } else {
            //进一步判断是否存在 LOGIN_USER_KEY
            if (session.getAttribute("LOGIN_USER") == null) {
                //说明，虽然存在session,但是用户并没有登录
                LOGGER.info("虽然有Session，但是没有登录...");
                resp.sendRedirect(req.getServletContext().getContextPath() + "/user/toLogin");
                //req.getRequestDispatcher("/user/toLogin").forward(req, resp);
            } else {
                LOGGER.debug("--- 用户已认证，放行通过LoginFilter...");
                //如果用户是已经登录过的，放行请求
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
