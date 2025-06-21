package com.mobile.studydocs.config;

import com.mobile.studydocs.filter.FirebaseAuthentication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<FirebaseAuthentication> filterRegistrationBean(FirebaseAuthentication filter) {
        FilterRegistrationBean<FirebaseAuthentication> bean = new FilterRegistrationBean<>();
        bean.setFilter(filter);
        bean.addUrlPatterns("/*");
        return bean;
    }
}
