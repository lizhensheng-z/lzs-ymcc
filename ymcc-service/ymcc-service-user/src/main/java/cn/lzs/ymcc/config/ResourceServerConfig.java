package cn.lzs.ymcc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

//ResourceServerConfigurer:资源服务器配置
//@EnableResourceServer:开启资源服务配置
@Configuration
@EnableResourceServer
public class ResourceServerConfig implements ResourceServerConfigurer {

    //2.3.配置Token的存储方案
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    //JWT令牌校验工具
    private final String sign_key = "123";

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(sign_key);
        return jwtAccessTokenConverter;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer configurer) throws Exception {
        configurer.resourceId("userId");
        configurer.tokenStore(tokenStore());
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers("/user/register", "/user/loginId/**").permitAll()  // 放行注册接口
                .antMatchers("/**", "/swagger-ui.html").permitAll()
                .and().csrf().disable();
    }
}