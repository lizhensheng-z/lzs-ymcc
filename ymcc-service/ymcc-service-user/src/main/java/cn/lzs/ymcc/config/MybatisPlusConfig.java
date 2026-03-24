package cn.lzs.ymcc.config;

import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/**
 * @author 李振生
 */

@Configuration
//mapper接口扫描
@MapperScan("cn.lzs.ymcc.mapper")
//事务管理
//@EnableTransactionManagement //注释掉，因为交给seata管理
public class MybatisPlusConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
