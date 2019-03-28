package com.gallery.configuration;

import com.gallery.model.directory.DirectoryWalkerI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.FileSystem;
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

    @Bean("currentDir")
    //@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = WebApplicationContext.SCOPE_SESSION)
    @Autowired
    public Path currentDir(DirectoryWalkerI directoryWalker) {
        LoggerFactory.getLogger(this.getClass()).trace("currentDir bean factory method was called");
        return directoryWalker.getRoot();
    }

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
