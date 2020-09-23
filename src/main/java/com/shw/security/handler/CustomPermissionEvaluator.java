package com.shw.security.handler;

import com.shw.security.domain.SysPermission;
import com.shw.security.service.SysPermissionService;
import com.shw.security.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/22 18:12
 * @description
 */
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private SysRoleService roleService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetUrl, Object targetPermission) {
        // 获得loadUserByUsername()方法的结果
        User user = (User) authentication.getPrincipal();
        // 获得loadUserByUsername中注入的角色
        Collection<GrantedAuthority> authorities = user.getAuthorities();

        // 遍历用户所有角色
        for (GrantedAuthority authority : authorities) {
            String roleName = authority.getAuthority();
            Integer roleId = roleService.selectByName(roleName).getId();

            // 得到角色所有的权限
            List<SysPermission> permissionList = permissionService.listByRoleId(roleId);

            // 遍历permissionList
            for (SysPermission sysPermission : permissionList) {
                // 获取权限集合
                String permission = sysPermission.getPermission();
                // 如果访问的Url和权限用户符合的话，返回true
                if (targetUrl.equals(sysPermission.getUrl()) && permission.contains((CharSequence) targetPermission)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
