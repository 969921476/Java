package com.crowd.service.api;

import com.crowd.entity.Auth;

import java.util.List;
import java.util.Map;

/**
 * @author cjn
 * @create 2020-03-17 9:08
 */
public interface AuthService {
    List<Auth> getAll();

    List<Integer> getAssignedAuthIdByRoleId(Integer roleId);

    void saveRoleAuthRelathinship(Map<String, List<Integer>> map);

    List<String> getAssignedAuthNameByAdminId(Integer adminId);
}
