package com.atypon.bootstrappingnode.config;

import com.atypon.bootstrappingnode.secuirty.AuthenticationFilter;
import com.atypon.bootstrappingnode.secuirty.AuthorizationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<AuthenticationFilter> AuthenticationFilterRegister(AuthenticationFilter authenticationFilter) {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authenticationFilter);
        registrationBean.addUrlPatterns("/api/access/*");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AuthorizationFilter> AuthorizationFilterRegister(AuthorizationFilter authorizationFilter) {
        FilterRegistrationBean<AuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authorizationFilter);
        registrationBean.addUrlPatterns("/api/cluster/*");
        return registrationBean;
    }

}
