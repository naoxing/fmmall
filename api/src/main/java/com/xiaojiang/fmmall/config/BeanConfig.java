package com.xiaojiang.fmmall.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author xiaojiang
 * @Date 2023-05-11 10:52
 * @Description
 **/
@Configuration
public class BeanConfig {
    @Bean
    public ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }
}
