package com.insightx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration
 * Configures static resource handling
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Disable default /** mapping that catches REST endpoints
        // Only handle specific static paths
        registry.setOrder(Integer.MAX_VALUE); // Low priority - let controllers handle first
        
        registry
                .addResourceHandler("/static/**", "/public/**", "/css/**", "/js/**", "/images/**")
                .addResourceLocations(
                        "classpath:/static/",
                        "classpath:/public/",
                        "classpath:/static/css/",
                        "classpath:/static/js/",
                        "classpath:/static/images/"
                );
    }
}
