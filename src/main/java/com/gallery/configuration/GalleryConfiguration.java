package com.gallery.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
public class GalleryConfiguration implements WebMvcConfigurer {

    @Value("${gallery.filesystem.root}")
    private String rootDir;

    @Bean("Logger")
    @Scope("prototype")
    public Logger logger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        LoggerFactory.getLogger(this.getClass())
                .debug("Static content is being served from file:" + rootDir);

        registry
                .addResourceHandler("/f/**")
                .addResourceLocations("file://" + rootDir + FileSystems.getDefault().getSeparator());
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

}
