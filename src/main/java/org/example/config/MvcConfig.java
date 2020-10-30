package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${upload.musclePath}")
    private String uploadMusclePath;

    @Value("${upload.muscleGroupPath}")
    private String uploadMuscleGroupPath;

    @Value("${upload.exercisePath}")
    private String uploadExercisePath;

    public void addViewControllers(ViewControllerRegistry registry) {
        //        registry.addViewController("/home").setViewName("home");
        ////        registry.addViewController("/").setViewName("home");
        //        registry.addViewController("/hello").setViewName("hello");
        //        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file://" + uploadMuscleGroupPath + "/")
                .addResourceLocations("file://" + uploadMusclePath + "/")
                .addResourceLocations("file://" +uploadExercisePath+"/");
    }
}
