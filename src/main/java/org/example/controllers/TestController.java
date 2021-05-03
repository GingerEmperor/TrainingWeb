package org.example.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.example.models.User;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.services.MuscleGroupService;
import org.example.services.MuscleService;
import org.example.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
// @Controller
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final MuscleService muscleService;

    private final MuscleGroupService muscleGroupService;

    private final UserService userService;

    @GetMapping()
    public String sayHello(){
        return "testPages/testGreetings";
    }

    @GetMapping("/muscles")
    public List<Muscle> showAllMuscles(){
        return muscleService.findAll().stream().limit(2L).collect(Collectors.toList());
    }


    @GetMapping("/muscleGroups")
    public List<MuscleGroup> showAllMuscleGroups(){
        return muscleGroupService.findAll().stream().limit(2L).collect(Collectors.toList());
    }

    @GetMapping("/users")
    public List<User> showAllUsers(){
        return userService.findAll();
    }



}
