package com.crowd.mvc.config;

import com.crowd.exception.LoginAcctAlreadyInUseException;
import com.crowd.exception.LoginAcctAlreadyInUseFroUpdateException;
import com.crowd.exception.LoginFailedException;
import com.crowd.util.CrowdConstant;
import com.crowd.util.CrowdUtil;
import com.crowd.util.ResultEntity;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author cjn
 * @create 2020-03-13 11:59
 */
// @ControllerAdvice 表示当前的类是一个基于注解的异常处理类
@ControllerAdvice
public class CrowdExceptionResolver {
    // ExceptionHandler 将一个具体的异常类型和一个方法关联起来
    @ExceptionHandler(value = LoginFailedException.class)
    public ModelAndView resolveLoginException(LoginFailedException exception, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String viewName = "admin-login";
        return commonResolve(viewName,exception,request,response);

    }
    @ExceptionHandler(value = Exception.class)
    public ModelAndView resolveException(Exception exception, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String viewName = "system-error";
        return commonResolve(viewName,exception,request,response);

    }


    // ExceptionHandler 将一个具体的异常类型和一个方法关联起来
    @ExceptionHandler(value = LoginAcctAlreadyInUseException.class)
    public ModelAndView resolveLoginAcctAlreadyInUseException(LoginAcctAlreadyInUseException exception, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String viewName = "admin-add";
        return commonResolve(viewName,exception,request,response);

    }

    // ExceptionHandler 将一个具体的异常类型和一个方法关联起来
    @ExceptionHandler(value = LoginAcctAlreadyInUseFroUpdateException.class)
    public ModelAndView resolveLoginAcctAlreadyInUseFroUpdateException(LoginAcctAlreadyInUseFroUpdateException exception, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String viewName = "system-error";
        return commonResolve(viewName,exception,request,response);

    }

    private ModelAndView commonResolve(String viewName, Exception exception,HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1.判断当前请求类型
        boolean judgeResult = CrowdUtil.judgeRequestType(request);
        if (judgeResult) {
            // 创建一盒对象
            ResultEntity<Object> resultEntity = ResultEntity.failed(exception.getMessage());
            // 创建Gson对象
            Gson gson = new Gson();
            String json = gson.toJson(resultEntity);

            // 将json作为响应体返回给浏览器
            response.getWriter().write(json);
            // 使用了response 所有不提供ModelAndView
            return null;
        }

        // 不是Ajax请求
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(CrowdConstant.ATTR_NAME_EXCEPTION, exception);
        modelAndView.setViewName(viewName);
        return modelAndView;
    }
}
