package org.example.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.example.exeptions.FileCanNotSaveException;
import org.example.exeptions.MuscleGroupNotFoundException;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.services.GlobalService;
import org.example.services.MuscleGroupService;
import org.example.services.MuscleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/muscleGroups")
public class MuscleGroupController {

    private final MuscleGroupService muscleGroupService;

    private final MuscleService muscleService;

    private final GlobalService globalService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping()
    public String showAllMusclesGroup(Model model) {
        List<MuscleGroup> allMusclesGroup = muscleGroupService.findAll();
        allMusclesGroup.sort((o1, o2) -> o1.getName().toLowerCase().charAt(0) - o2.getName().toLowerCase().charAt(0));
        model.addAttribute("muscleGroups", allMusclesGroup);
        return "muscleTemplates/muscleGroups";
    }

    @GetMapping("/{id}")
    public String showMusclesByGroupId(@PathVariable Long id,
            Model model) {
        Map<MuscleGroup, List<Muscle>> muscleGroupMusclesMap = new TreeMap<>();
        MuscleGroup currentMuscleGroup;
        try {
            currentMuscleGroup = muscleGroupService.findById(id);
        } catch (MuscleGroupNotFoundException e) {
            e.printStackTrace();
            return "redirect:/";
        }
        List<Muscle> muscles = muscleService.findAllByMuscleGroup(currentMuscleGroup);
        muscles.sort((o1, o2) -> o1.getName().toLowerCase().charAt(0) - o2.getName().toLowerCase().charAt(0));
        muscleGroupMusclesMap.put(currentMuscleGroup, muscles);

        model.addAttribute("muscleGroupMusclesMap", muscleGroupMusclesMap);
        model.addAttribute("currentMuscleGroup", currentMuscleGroup);

        return "muscleTemplates/muscles";
    }

    @GetMapping("/all")
    public String showAllMuscles() {
        return "redirect:/muscles";
    }

    @PostMapping("/add")
    public String addMuscleGroup(@RequestParam(name = "muscleGroup") String muscleGroupName,
            @RequestParam(name = "file") MultipartFile file
    ) {

        if (muscleGroupName.isEmpty()) {
            return "redirect:/";
        }

        try {
            muscleGroupService.createNewMuscleGroup(muscleGroupName, file);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
        return "redirect:/muscleGroups";
    }

    @PostMapping("/{id}/delete")
    public String deleteMuscleGroup(@PathVariable(name = "id") Long muscleGroupId) {
        try {
            MuscleGroup muscleGroupToDelete = muscleGroupService.findById(muscleGroupId);
            muscleGroupService.deleteMuscleGroup(muscleGroupToDelete);
        } catch (Exception e) {
            System.out.println("OSIBKA");//TODO add some handler
            e.printStackTrace();
            return "redirect:/";
        }
        return "redirect:/muscleGroups";
    }

}
