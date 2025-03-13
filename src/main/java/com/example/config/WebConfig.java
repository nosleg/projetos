package com.example.config;

import com.example.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Registra o interceptor e define os padrões de URL que ele deve interceptar
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/contas/**"); // Intercepta todas as rotas que começam com /contas/
    }
}