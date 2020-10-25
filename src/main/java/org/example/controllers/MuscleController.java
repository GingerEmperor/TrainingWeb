package org.example.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.example.exeptions.FileCanNotSaveException;
import org.example.models.Exercise;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.services.GlobalService;
import org.example.services.MuscleGroupService;
import org.example.services.MuscleService;
import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping("/muscles")
@RequiredArgsConstructor
public class MuscleController {

    private final MuscleService muscleService;

    private final MuscleGroupService muscleGroupService;

    private final GlobalService globalService;

    // @Autowired
    // CSVService csvService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping()
    public String showAllMuscles(Model model) {
        List<MuscleGroup> allMuscleGroups =muscleGroupService.findAll();
        model.addAttribute("muscleGroups",allMuscleGroups);
        return "muscles";
    }

    @GetMapping("/{id}")
    public String muscleByIdDetails(@PathVariable(name = "id") long id,
            Model model) {
        model.addAttribute("muscle", muscleService.findById(id));
        return "muscleDetails";
    }

    @PostMapping("/add")
    public String addMuscle(@RequestParam(name = "muscleGroup") String muscleGroup,
            @RequestParam(name = "muscleName") String muscleName,
            @RequestParam(name = "muscle_info") String info,
            @RequestParam(name = "groupId") Long groupId,
            @RequestParam(name = "file") MultipartFile file
    ) throws IOException {

        Muscle currentMuscle = muscleService.findByName(muscleName);
        if (currentMuscle != null) {
            throw new RuntimeException("Такая мышца уже существует");
        }

        MuscleGroup currentMuscleGroup = muscleGroupService.findByName(muscleGroup);
        if (currentMuscleGroup == null) {
            throw new RuntimeException("Такой группы мышц не существует");
        }

        try {
            currentMuscle = muscleService.createMuscle(
                    currentMuscleGroup, muscleName, info,
                    globalService.saveImgToPathWithPrefixName(file, uploadPath, muscleName)
            );
        } catch (FileCanNotSaveException e) {
            currentMuscle = muscleService.createMuscle(
                    currentMuscleGroup, muscleName, info
            );

        }
        muscleService.save(currentMuscle);
        muscleGroupService.addMusclesIntoGroup(currentMuscle, currentMuscleGroup);

        return "redirect:/muscleGroups/" + groupId;
    }



    //TODO add delete image
    @PostMapping("/{id}/delete")
    public String deleteMuscleById(@PathVariable long id,
            @RequestParam(name = "groupId") Long groupId) {
        muscleService.deleteMuscleById(id);
        return "redirect:/muscleGroups/" + groupId;
    }

    // @PostMapping("/all/toCSV")//TODO write to CSV
    // public String allMusclesToCSV(){
    //     System.out.println();
    //     // csvService.writeToCsvFile(muscleService.findAll().stream().,",");
    // }

    // @GetMapping("/csv")
    // public String allMusclesToCSV(){
    //     System.out.println();
    //     Iterable<Muscle> muscles=muscleService.findAll();
    //     muscles.forEach(System.out::println);
    //     return "redirect:/";
    // }
}
