package cn.lzs.ymcc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

//资源服务器配置 - 禁用安全验证
@Configuration
@EnableWebSecurity
public class ResourceServerConfig {

    public void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .anyRequest().permitAll();
    }
}