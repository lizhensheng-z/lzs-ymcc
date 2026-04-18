package cn.lzs.ymcc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson配置类
 * 解决Long类型在前端精度丢失的问题
 * 
 * 问题原因：
 * JavaScript的Number类型最大安全整数是2^53-1（9007199254740991）
 * 而雪花算法生成的Long类型ID通常是19位，远超JS的安全范围
 * 导致前端接收到的ID精度丢失
 * 
 * 解决方案：
 * 将Long类型字段在JSON序列化时转为String类型
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        
        // 将Long类型序列化为String
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        
        objectMapper.registerModule(simpleModule);
        
        return objectMapper;
    }
}
