package com.crowd.service.impl;

import com.crowd.entity.Auth;
import com.crowd.entity.AuthExample;
import com.crowd.mapper.AuthMapper;
import com.crowd.service.api.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author cjn
 * @create 2020-03-17 9:09
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;

    @Override
    public List<Auth> getAll() {
        return authMapper.selectByExample(new AuthExample());
    }

    @Override
    public List<Integer> getAssignedAuthIdByRoleId(Integer roleId) {
        return authMapper.selectAssignedAuthIdByRoleId(roleId);
    }

    @Override
    public void saveRoleAuthRelathinship(Map<String, List<Integer>> map) {
        // 获取 roleId
        List<Integer> roleIdList = map.get("roleId");
        Integer roleId = roleIdList.get(0);

        // 删除旧的关联关系
        authMapper.deleteOldRelathinship(roleId);

        // 获取authList
        List<Integer> authIdList = map.get("authIdArray");
        if (authIdList != null && authIdList.size() > 0) {
            authMapper.insertNewRelathinship(roleId, authIdList);
        }
    }

    @Override
    public List<String> getAssignedAuthNameByAdminId(Integer adminId) {
        return authMapper.selectAssignedAuthNameByAdminId(adminId);
    }
}
