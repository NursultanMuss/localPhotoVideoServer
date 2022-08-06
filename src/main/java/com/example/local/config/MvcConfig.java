package com.example.local.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.example.local.services.impl.SearchServiceImpl.WINDOWS;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    String uploadPath;
    @Value("${windows.user.path}")
    private String windowsPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String osName = System.getProperty("os.name");
        String homePath = "";
        if(osName.startsWith(WINDOWS)){
            homePath = "/" + windowsPath;
        }else{
            homePath = uploadPath;
        }
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file://" + homePath + "/");
    }
}
