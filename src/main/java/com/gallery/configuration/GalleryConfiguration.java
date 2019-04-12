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

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableAspectJAutoProxy
@EnableWebMvc
public class GalleryConfiguration implements WebMvcConfigurer {

    @Value("${gallery.filesystem.rootDir}")
    private String rootDir;

    @Value("${gallery.filesystem.thumbnailDir}")
    private String thumbnailDir;

    @Bean("Logger")
    @Scope("prototype")
    public Logger logger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
    }

    @Bean("rootDir")
    public Path rootDir(@Value("${gallery.filesystem.rootDir}") String path) {
        LoggerFactory.getLogger(this.getClass()).trace("rootDir bean factory method was called");
        return Paths.get(path);
    }
/*

    @Bean
    UserPreferences buildUserPreferences(String path) {
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setCurrentDir(Paths.get(path));
        return userPreferences;
    }
*/


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        LoggerFactory.getLogger(this.getClass())
                .debug("Static content is being served from file:" + rootDir);

        registry.addResourceHandler("/t/**")
                .addResourceLocations("file://" + thumbnailDir + FileSystems.getDefault().getSeparator());

        registry.addResourceHandler("/f/**")
                .addResourceLocations("file://" + rootDir + FileSystems.getDefault().getSeparator());

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

}
