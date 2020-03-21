package com.crowd.mvc.config;

import com.crowd.util.CrowdConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * SpringSecurity 的配置类
 *
 * @author cjn
 * @create 2020-03-17 16:43
 *
 */
@Configuration
@EnableWebSecurity  // 启动SpringSecurity 由SpringMVC进行扫描
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 启动全局方法权限控制功能
public class WebAppSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

//    @Bean   只会在SpringMVC的IOC容器中
//    public BCryptPasswordEncoder getPasswordEncoder() {
//        return  new BCryptPasswordEncoder();
//    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {

        //builder.inMemoryAuthentication().withUser("root").password("cjn023134").roles("ADMIN");
        builder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security
                .authorizeRequests()                                    // 对请求进行授权
                .antMatchers("/admin/to/login/page.html")  // 登录页
                .permitAll()                                            // 无条件访问
                .antMatchers("/crowd/**")                  // 静态资源进行访问
                .permitAll()
                .antMatchers("/css/**")
                .permitAll()
                .antMatchers("/fonts/**")
                .permitAll()
                .antMatchers("/img/**")
                .permitAll()
                .antMatchers("/jquery/**")
                .permitAll()
                .antMatchers("/layer/**")
                .permitAll()
                .antMatchers("/bootstrap/**")
                .permitAll()
                .antMatchers("/script/**")
                .permitAll()
                .antMatchers("/ztree/**")
                .permitAll()
                .antMatchers("/admin/get/page.html")
//                .hasRole("经理")
                .access("hasRole('经理') OR hasAuthority('user:get')")
                .anyRequest()                                          // 对其他的请求
                .authenticated()                                       // 都要进行验证
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                        httpServletRequest.setAttribute("exception" , new Exception(CrowdConstant.MESSAGE_ACCESS_DENIED));
                        httpServletRequest.getRequestDispatcher("/WEB-INF/system-error.jsp").forward(httpServletRequest,httpServletResponse);
                    }
                })
                .and()
                .csrf()                                                // 防跨站访问请求伪造功能
                .disable()
                .formLogin()					                       // 开启表单登录的功能
                .loginPage("/admin/to/login/page.html")	               // 指定登录页面
                .loginProcessingUrl("/security/do/login.html")	       // 指定处理登录请求的地址
                .defaultSuccessUrl("/admin/to/main/page.html")
//                .successForwardUrl("/admin/to/main/page.html")// 指定登录成功后前往的地址
                .usernameParameter("loginAcct")	                       // 账号的请求参数名称
                .passwordParameter("userPswd")	                       // 密码的请求参数名称
                .and()
                .logout()						                       // 开启退出登录功能
                .logoutUrl("/security/do/logout.html")			       // 指定退出登录地址
                .logoutSuccessUrl("/admin/to/login/page.html")	       // 指定退出成功以后前往的地址
        ;
    }
}
