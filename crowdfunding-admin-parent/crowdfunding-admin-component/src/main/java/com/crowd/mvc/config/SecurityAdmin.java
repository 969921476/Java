package com.crowd.mvc.config;

import com.crowd.entity.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/**
 * 考虑到User对象中仅仅包含账号和密码，为了可以获取到原始的Admin对象，专门创建这个类的User类 进行扩展
 * @author cjn
 * @create 2020-03-17 18:41
 */
public class SecurityAdmin extends User {
    private Admin originalAdmin;
    public SecurityAdmin(Admin originalAdmin, List<GrantedAuthority> authorities) {
        super(originalAdmin.getLoginAcct(),  originalAdmin.getUserPswd(), authorities);
        this.originalAdmin = originalAdmin;

        // 将原始Admin 对象中的密码查出
        this.originalAdmin.setUserPswd(null);
    }

    public Admin getOriginalAdmin() {
        return originalAdmin;
    }
}
