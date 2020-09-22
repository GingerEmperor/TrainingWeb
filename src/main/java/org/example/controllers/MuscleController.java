package org.example.controllers;

import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.repository.MuscleGroupRepository;
import org.example.services.MuscleGroupService;
import org.example.services.MuscleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/muscles")
public class MuscleController {
    @Autowired
    MuscleService muscleService;
    @Autowired
    MuscleGroupService muscleGroupService;

    @GetMapping()
    public String showAllMuscles(Model model) {
        Iterable<Muscle> allMuscles = muscleService.findAll();
        model.addAttribute("allMuscles", allMuscles);
        return "muscles";
    }

    @PostMapping("/add")
    public String addMuscle(@RequestParam(name = "muscleGroup") String  muscleGroup,
                            @RequestParam(name = "muscleName") String muscleName,
                            @RequestParam(name = "muscle_info") String info,
                            @RequestParam(name = "groupId") Long groupId
    ) {

        Muscle currentMuscle=muscleService.findByName(muscleName);
        if (currentMuscle != null) {
            throw new RuntimeException("Такая мышца уже существует");
        }

        MuscleGroup currentMuscleGroup=muscleGroupService.findByName(muscleGroup);
        if(currentMuscleGroup==null ){
            throw new RuntimeException("Такой группы мышц не существует");
        }
            currentMuscle= muscleService.addMuscle(currentMuscleGroup, muscleName, info);
            muscleGroupService.addMusclesIntoGroup(currentMuscle,currentMuscleGroup);

        return "redirect:/muscleGroups/"+groupId;
    }

    @GetMapping("/{id}")
    public String muscleByIdDetails(@PathVariable(name = "id") long id,
                                    Model model) {

        model.addAttribute("muscle", muscleService.findById(id));
        return "muscleDetails";
    }

    @PostMapping("/{id}/delete")
    public String deleteMuscleById(@PathVariable long id,
                                   @RequestParam(name = "groupId") Long groupId) {
        muscleService.deleteMuscleById(id);
        return "redirect:/muscleGroups/"+groupId;
    }
}
