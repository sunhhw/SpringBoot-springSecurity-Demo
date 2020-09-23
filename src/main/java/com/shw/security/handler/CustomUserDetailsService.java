package com.shw.security.handler;

import com.shw.security.domain.SysRole;
import com.shw.security.domain.SysUser;
import com.shw.security.domain.SysUserRole;
import com.shw.security.service.SysRoleService;
import com.shw.security.service.SysUserRoleService;
import com.shw.security.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/22 11:32
 * @description
 * 自定义 UserDetailsService,将用户信息和权限注入进来。
 *
 * 我们需要重写 loadUserByUsername 方法，参数是用户输入的用户名。
 * 返回值是UserDetails，这是一个接口，一般使用它的子类org.springframework.security.core.userdetails.User，它有三个参数，分别是用户名、密码和权限集。
 *
 */
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysUserRoleService userRoleService;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        //从数据库中取出用户信息
        SysUser sysUser = userService.selectByName(username);

        // 判断是否存在
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 添加权限
        List<SysUserRole> sysUserRoles = userRoleService.listByUserId(sysUser.getId());
        for (SysUserRole sysUserRole : sysUserRoles) {
            SysRole sysRole = roleService.selectById(sysUserRole.getRoleId());
            authorities.add(new SimpleGrantedAuthority(sysRole.getName()));
        }

        // 返回UserDetails实现类
        // 这里从数据库拿到的密码跟传到前端作比较
        return new User(sysUser.getName(), sysUser.getPassword(),authorities);

    }





}
