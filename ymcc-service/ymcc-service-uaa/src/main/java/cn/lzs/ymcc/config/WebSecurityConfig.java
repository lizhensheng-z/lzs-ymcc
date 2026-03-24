package cn.lzs.ymcc.config;

import cn.lzs.ymcc.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled= true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private PermissionMapper permissionMapper;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    //密码编码器：不加密
    //实际项目中应该使用加密密码，这里为了演示方便，不使用加密密码
    // TODO 换成加密密码
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    //授权规则配置
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Web授权 从数据库查询所有权限
//        List<Permission> permissions = permissionMapper.selectList(null);
//        for(Permission permission : permissions){
//            System.out.println(permission.getResource()+" - "+permission.getSn());
//            //如： /employee/list    需要     employee:list 权限才能访问
//            //循环后进行一一授权
//            http.authorizeRequests().antMatchers(permission.getResource()).hasAuthority(permission.getSn());
//        }
        http.authorizeRequests()                                //授权配置
                .antMatchers("/**","/login","/login/common","/v2/api-docs","/login/refresh").permitAll()  //登录路径放行
//                .anyRequest().authenticated()                   //其他路径都要认证之后才能访问
                .and().formLogin()                              //允许表单登录
                .successForwardUrl("/login/success")             // 设置登陆成功页 修改为自己的
                .and().logout().permitAll()   //登出路径放行 /logout
                //正常情况下默认开启跨域伪造检查，
                //这里关闭跨域伪造检查，方便测试 我们项目也不使用
                .and().csrf().disable();                        //关闭跨域伪造检查
    }

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("123"));

    }
}