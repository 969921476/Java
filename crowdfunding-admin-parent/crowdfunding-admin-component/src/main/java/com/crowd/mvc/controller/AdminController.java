package com.crowd.mvc.controller;

import com.crowd.entity.Admin;
import com.crowd.service.api.AdminService;
import com.crowd.util.CrowdConstant;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @author cjn
 * @create 2020-03-13 15:10
 */
@Controller
public class AdminController {
    @Autowired
    private AdminService adminService;

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    @RequestMapping("/admin/update.html")
    public String update(Admin admin, @RequestParam("pageNum") Integer pageNum, @RequestParam("keyword") String keyword) {

        adminService.update(admin);

        return "redirect:/admin/get/page.html?pageNum=" + pageNum + "&keyword=" + keyword;
    }

    @RequestMapping("/admin/to/edit/page.html")
    public String toEditPage(@RequestParam("adminId") Integer adminId,
                             ModelMap modelMap) {
        // 根据Id查询Admin对象
        Admin admin = adminService.getAdminById(adminId);
        modelMap.addAttribute("admin", admin);
        return "admin-edit";
    }

    @PreAuthorize("hasAuthority('user:save')")
    @RequestMapping("/admin/save.html")
    public String save(Admin admin) {
        adminService.saveAdmin(admin);

        return "redirect:/admin/get/page.html?pageNum=" + Integer.MAX_VALUE;
    }

    @RequestMapping("admin/remove/{adminId}/{pageNum}/{keyword}.html")
    public String remove(
            @PathVariable("adminId") Integer adminId,
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("keyword") String keyword
    ) {
        // 执行删除
        adminService.remove(adminId);

        // 页面跳转
        return "redirect:/admin/get/page.html?pageNum=" + pageNum + "&keyword=" + keyword;
    }


    @RequestMapping("/admin/get/page.html")
    public String getPageInfo(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            ModelMap modelMap) {
        // 调用Service获取PageInfo
        PageInfo<Admin> pageInfo = adminService.getPageInfo(keyword, pageNum, pageSize);

        // 把PageInfo放入模型中
        modelMap.addAttribute(CrowdConstant.ATTR_NAME_PAGE_INFO, pageInfo);

        return "admin-page";
    }


    @RequestMapping("/admin/do/logout.html")
    public String doLogout(HttpSession session) {
        //强制Session失效
        session.invalidate();

        return "redirect:/admin/to/login/page.html";
    }


    @RequestMapping("/admin/do/login.html")
    public String doLogin(@RequestParam("loginAcct") String loginAcct, @RequestParam("userPswd") String userPswd, HttpSession session){
//        logger.info("doLogin" + "进入登录执行方法");
        // 调用Service 执行登录检查
        Admin admin = adminService.getAdminByLoginAcct(loginAcct, userPswd);

        //登录成功的admin存入Session中
        session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_ADMIN, admin);
//        logger.info("doLogin" + "离开登录执行方法");
        return "redirect:/admin/to/main/page.html";
    }
}
