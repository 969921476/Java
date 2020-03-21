package com.crowd.mvc.controller;

import com.crowd.entity.Auth;
import com.crowd.entity.Role;
import com.crowd.service.api.AdminService;
import com.crowd.service.api.AuthService;
import com.crowd.service.api.RoleService;
import com.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


/**
 * @author cjn
 * @create 2020-03-16 14:28
 */
@Controller
public class AssignController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthService authService;
    @ResponseBody
    @RequestMapping("/assign/get/assigned/auth/id/by/role/id.json")
    public ResultEntity<List<Integer>> getAssignedAuthIdByRoleId(@RequestParam("roleId") Integer roleId) {

        List<Integer> authIdList =  authService.getAssignedAuthIdByRoleId(roleId);

        return ResultEntity.successWithData(authIdList);
    }

    @ResponseBody
    @RequestMapping("/assign/do/role/assign/auth.json")
    public ResultEntity<String> saveRoleAuthRelathinship(@RequestBody Map<String, List<Integer>> map) {

        authService.saveRoleAuthRelathinship(map);

        return ResultEntity.successWithoutData();
    }

    @RequestMapping("/assign/get/all/auth.json")
    @ResponseBody
    public ResultEntity<List<Auth>> getAllAuth(){
        List<Auth> authList = authService.getAll();
        return ResultEntity.successWithData(authList);
    }


    @RequestMapping("/assign/do/role/assign.html")
    public String saveAdminRoleRelationShip(
            @RequestParam("adminId") Integer adminId,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam(value = "keyword") String keyword,
            // required 是否允许没有值
            @RequestParam(value = "roleIdList", required = false) List<Integer> roleIdList
    ){


        adminService.saveAdminRoleRelationShip(adminId, roleIdList);

        return "redirect:/admin/get/page.html?pageNum="+pageNum +"&keyword="+keyword;
    }


    @RequestMapping("/assign/to/assign/role/page.html")
    public String toAssignRolePage(
            @RequestParam("adminId") Integer adminId,
            ModelMap modelMap
    ) {

        // 查询已分配的角色
        List<Role> assignRoleList = roleService.getAssignRole(adminId);

        // 查询未分配的角色
        List<Role> unAssignRoleList = roleService.getUnAssignRole(adminId);

        // 存入模型
        modelMap.addAttribute("assignRoleList",assignRoleList);
        modelMap.addAttribute("unAssignRoleList",unAssignRoleList);

        return "assign-role";
    }
}
