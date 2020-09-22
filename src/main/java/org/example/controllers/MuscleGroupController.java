package org.example.controllers;

import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.services.MuscleGroupService;
import org.example.services.MuscleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("/muscleGroups")
public class MuscleGroupController {
    @Autowired
    MuscleGroupService muscleGroupService;
    @Autowired
    MuscleService muscleService;

    @GetMapping()
    public String showAllMusclesGroup(Model model) {
        Iterable<MuscleGroup> allMusclesGroup = muscleGroupService.findAll();
        model.addAttribute("allMusclesGroup", allMusclesGroup);
        return "muscleGroups";
    }

    @GetMapping("/{id}")
    public String showMusclesByGroupId(@PathVariable Long id,
                                       Model model) {
        MuscleGroup currentMuscleGroup = muscleGroupService.findMuscleGroupById(id);
        Set<Muscle> muscles = muscleService.findAllByMuscleGroup(currentMuscleGroup);
        model.addAttribute("allMuscles", muscles);
        model.addAttribute("currentMuscleGroup",currentMuscleGroup);
        return "muscles";
    }

    @GetMapping("/all")
    public String showAllMuscles() {
        return "redirect:/muscles";
    }

    @PostMapping("/add")
    public String addMuscleGroup(@RequestParam(name = "muscleGroup") String muscleGroup) {
        if (muscleGroup.isEmpty())
            return "redirect:/";
        MuscleGroup currentMuscleGroup = muscleGroupService.findByName(muscleGroup);
        if (currentMuscleGroup != null) {
            throw new RuntimeException("Такая мышечная группа уже есть");
        }
        muscleGroupService.createNewMuscleGroup(muscleGroup);
        return "redirect:/muscleGroups";
    }

    @PostMapping("/{id}/delete")
    public String deleteMuscleGroup(@PathVariable(name = "id") Long muscleGroupId) {
        MuscleGroup muscleGroupToDelete = muscleGroupService.findMuscleGroupById(muscleGroupId);
        if (muscleGroupToDelete == null)
            throw new RuntimeException("Невозможно удалить. Такой группы нет");
        if(!muscleGroupToDelete.getMuscleSet().isEmpty())
            throw new RuntimeException("Невозможно удалить. Очистите группу");
        muscleGroupService.deleteMuscleGroup(muscleGroupToDelete);
        return "redirect:/muscleGroups";
    }

}
