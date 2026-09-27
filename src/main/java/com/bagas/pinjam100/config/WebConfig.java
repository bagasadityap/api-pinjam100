package com.bagas.pinjam100.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.file-dir}")
    private String fileDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String location = Paths
                .get(fileDir)
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();

        registry
                .addResourceHandler("/uploads/files/**")
                .addResourceLocations(location);
    }
}