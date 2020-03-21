package com.crowd.mvc.config;

import com.crowd.entity.Admin;
import com.crowd.entity.Role;
import com.crowd.service.api.AdminService;
import com.crowd.service.api.AuthService;
import com.crowd.service.api.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于从数据库获取数据登录
 * @author cjn
 * @create 2020-03-17 18:49
 */
@Component
public class CrowdUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthService authService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据账号查询Admin对象
        Admin admin = adminService.getAdminByLoginAcct(username);
        // 获取adminid
        Integer adminId = admin.getId();
        // 根据adminId查询角色信息
        List<Role> roleList = roleService.getAssignRole(adminId);
        // 根据adminId 查询权限的信息
        List<String> authNameList = authService.getAssignedAuthNameByAdminId(adminId);

        // 创建集合对象用来存储GrantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<>();
        // roleList 存入角色信息
        for (Role role:roleList) {
            // 注意：不要忘了加前缀
            String roleName = "ROLE_" + role.getName();

            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(roleName);

            authorities.add(simpleGrantedAuthority);
        }

        // 遍历authNameList存入权限信息
        for (String authName: authNameList) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authName);

            authorities.add(simpleGrantedAuthority);
        }

        // 封装SecurityAdmin
        SecurityAdmin securityAdmin = new SecurityAdmin(admin, authorities);
        return securityAdmin;
    }
}
