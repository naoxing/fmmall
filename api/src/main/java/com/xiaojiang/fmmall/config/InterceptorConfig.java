package com.xiaojiang.fmmall.config;

import com.xiaojiang.fmmall.interceptor.CheckTokenInterceptor;
import com.xiaojiang.fmmall.interceptor.SetTimeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author xiaojiang
 * @Date 2023-04-03 11:12
 * @Description 配置拦截器
 **/
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private CheckTokenInterceptor checkTokenInterceptor;
    @Autowired
    private SetTimeInterceptor setTimeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(checkTokenInterceptor)
                .excludePathPatterns("/user/**")
                .addPathPatterns("/user/check")
                .addPathPatterns("/shopcart/**")
                .addPathPatterns("/useraddr/**")
                .addPathPatterns("/order/**");
        registry.addInterceptor(setTimeInterceptor)
                .addPathPatterns("/**");
    }
}
