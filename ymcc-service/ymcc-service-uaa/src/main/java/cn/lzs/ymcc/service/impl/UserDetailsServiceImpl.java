package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.domain.Permission;
import cn.lzs.ymcc.service.ILoginService;
import cn.lzs.ymcc.service.IPermissionService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author 李振生
 * Spring Secruity
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private ILoginService loginService;
    @Autowired
    private IPermissionService permissionService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 换成真实数据库查询 用户信息  返回user
        EntityWrapper<Login> wrapper = new EntityWrapper<>();
        wrapper.eq("username", username);
        Login login = loginService.selectOne(wrapper);
        if(login == null){
            throw new GlobalBusinessException(SystemConstants.USER_IS_NOT_EXIST);
        }
        //根据login id查询用户信息
        //String username, String password, boolean enabled, boolean accountNonExpired,
        // boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        //查询用户权限，将用户权限赋给authorities
        ArrayList<Permission> permissionList = permissionService.getPermissionByLoginId(login.getId());
        for (Permission permission : permissionList) {
            authorities.add(new SimpleGrantedAuthority(permission.getSn()));
        }
        //封装需要的字段，扔到User.username
        Login login1 = new Login();
        login1.setUsername(login.getUsername());
        login1.setId(login.getId());
        //login1转为jsonString
        String jsonString = JSONObject.toJSONString(login1);
        User user = new User(jsonString,
                            login.getPassword(),
                            login.getEnabled(),
                            login.getAccountNonExpired(),
                            login.getCredentialsNonExpired(),
                            login.getAccountNonLocked(),
                            authorities);
        return user;
    }
}
