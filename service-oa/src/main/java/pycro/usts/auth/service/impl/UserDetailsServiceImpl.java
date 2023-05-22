package pycro.usts.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pycro.usts.auth.service.SysMenuService;
import pycro.usts.auth.service.SysUserService;
import pycro.usts.model.system.SysUser;
import pycro.usts.security.custom.CustomUser;
import pycro.usts.security.custom.UserDetailsService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-22 10:30 AM
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getUserByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if (sysUser.getStatus() == 0) {
            throw new RuntimeException("账号已停用");
        }
        // 根据userId查询权限列表
        List<String> permList = sysMenuService.findPermsByUserId(sysUser.getId());
        // 封装成权限列表
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        // 遍历添加
        for (String perm : permList) {
            authList.add(new SimpleGrantedAuthority(perm.trim()));
        }
        return new CustomUser(sysUser, authList);
    }
}
