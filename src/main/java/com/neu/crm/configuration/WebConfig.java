package com.neu.crm.configuration;

import com.neu.crm.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor).excludePathPatterns("/login","/register","**/build/**","**/css/**","**/dist/**",
                "**/docs/**","**/js/**","**/png/**","**/pages/**","**/plugins/**",
                "/**/*.css", "/**/*.css.map","/**/*.js", "/**/*.png", "/**/*.jpg","/**/*.ico",
                "/**/*.jpeg", "/**/*.gif", "/**/fonts/*", "/**/*.svg","/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/valueCalculating/**","/**").addResourceLocations("classpath:/static/");
    }

//    @Bean
//    public HttpMessageConverter<String> responseBodyConverter(){
//        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
//    }
//
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        WebMvcConfigurer.super.configureMessageConverters(converters);
//        converters.add(responseBodyConverter());
//    }

}
