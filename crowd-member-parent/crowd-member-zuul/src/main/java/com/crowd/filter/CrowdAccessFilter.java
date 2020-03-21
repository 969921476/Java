package com.crowd.filter;

import com.crowd.util.AccessPassResources;
import com.crowd.util.CrowdConstant;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.http.protocol.RequestContent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.renderable.RenderContext;
import java.io.IOException;

/**
 * @author cjn
 * @create 2020-03-20 16:52
 */
@Component
public class CrowdAccessFilter extends ZuulFilter {
    @Override
    public String filterType() {
        // 在目标微服务前执行过滤
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        // 获取RequestContext对象
        RequestContext renderContext = RequestContext.getCurrentContext();

        // renderContext获取当前请求对象 (底层是借助ThreadLocal从当前线程获取对象)
        HttpServletRequest request = renderContext.getRequest();

        // 获取ServletPath值
        String servletPath = request.getServletPath();

        // 判断当前请求是否放行当前特定的功能
        boolean containsResult = AccessPassResources.PASS_RES_SET.contains(servletPath);

        if (containsResult) {
            // 如果只可以直接放行的请求则返回false放行
            return false;
        }

        // 当前请求是否为静态资源
        return !AccessPassResources.judgeCurrentServletPathWetherStaticResource(servletPath);
    }

    @Override
    public Object run() throws ZuulException {
        // 获取当前请求
        RequestContext renderContext = RequestContext.getCurrentContext();
        HttpServletRequest request = renderContext.getRequest();

        // 获取当前Session
        HttpSession session = request.getSession();

        // 从Session获取对象
        Object attribute = session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);

        // 判断attribute是否为空
        if (attribute == null) {
            // 获取Response
            HttpServletResponse response = renderContext.getResponse();

            session.setAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_ACCESS_FORBIDEN);

            // 重定向到页面
            try {
                response.sendRedirect("/auth/member/to/login/page");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
