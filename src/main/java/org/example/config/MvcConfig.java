package org.example.config;

import org.example.models.dtos.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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

    @Value("${upload.trainingPath}")
    private String uploadTrainingPath;

    //todo update user images path
    @Value("${upload.userImagesPath}")
    private String uploadUserImagesPath;

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
                .addResourceLocations("file://" +uploadExercisePath+"/")
                .addResourceLocations("file://" +uploadUserImagesPath+"/")
                .addResourceLocations("file://" +uploadTrainingPath+"/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/images/");
        registry.addResourceHandler("/images/all/**")
                .addResourceLocations("classpath:/images/all/");

        registry.addResourceHandler("/images/all/muscleGroups/**")
                .addResourceLocations("classpath:/images/all/muscleGroups/");

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
