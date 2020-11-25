package org.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/training-elements")
public class TrainingElementsController {

    @GetMapping()
    public String showAll(){
        return "trainingTemplates/trainingElements";
    }

}
