package com.crowd.mvc.interceptor;

import com.crowd.entity.Admin;
import com.crowd.exception.AccessForbiddenException;
import com.crowd.util.CrowdConstant;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author cjn
 * @create 2020-03-13 16:19
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        // 获取Session对象
        HttpSession session = httpServletRequest.getSession();

        // 尝试通过Session获取Admin对象
        Admin admin = (Admin) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_ADMIN);

        // 判断Admin是否为空
        if (admin == null) {
            // 抛出异常
            throw new AccessForbiddenException(CrowdConstant.MESSAGE_ACCESS_FORBIDEN);
        }

        // 如果不为空返回true
        return true;
    }
}
