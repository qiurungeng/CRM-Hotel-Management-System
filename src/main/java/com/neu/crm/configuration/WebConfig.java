package com.neu.crm.configuration;

import com.neu.crm.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor).excludePathPatterns("/login","/register","**/build/**","**/css/**","**/dist/**",
                "**/docs/**","**/js/**","**/pages/**","**/plugins/**",
                "/**/*.css", "/**/*.css.map","/**/*.js", "/**/*.png", "/**/*.jpg","/**/*.ico",
                "/**/*.jpeg", "/**/*.gif", "/**/fonts/*", "/**/*.svg","/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/valueCalculating/**","/**").addResourceLocations("classpath:/static/");
    }
}
