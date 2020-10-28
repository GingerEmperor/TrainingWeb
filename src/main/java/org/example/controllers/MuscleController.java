package org.example.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.example.exeptions.CanNotDeleteException;
import org.example.exeptions.NotFoundException;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.services.GlobalService;
import org.example.services.MuscleGroupService;
import org.example.services.MuscleService;
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

    // @Autowired
    // CSVService csvService;

    @GetMapping()
    public String showAllMuscles(Model model) {
        List<MuscleGroup> allMuscleGroups = muscleGroupService.findAll();
        allMuscleGroups.sort((o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));

        Map<MuscleGroup, List<Muscle>> muscleGroupMusclesMap = new LinkedHashMap<>();
        for (MuscleGroup mG : allMuscleGroups) {
            List<Muscle> muscles = new ArrayList<>();
            for (Muscle m : mG.getMuscleSet()) {
                muscles.add(m);
            }
            muscles.sort((o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));
            muscleGroupMusclesMap.put(mG, muscles);
        }
        model.addAttribute("muscleGroupMusclesMap", muscleGroupMusclesMap);
        return "muscleTemplates/muscles";
    }

    @GetMapping("/{id}")
    public String muscleByIdDetails(@PathVariable(name = "id") long id,
            Model model) {

        try {
            Muscle muscle = muscleService.findById(id);
            model.addAttribute("muscle", muscle);
            return "muscleTemplates/muscleDetails";
        } catch (NotFoundException e) {
            System.out.println("OSIBKA");
            e.printStackTrace();
            return "redirect:/";
        }

    }

    @PostMapping("/add")
    public String addMuscle(@RequestParam(name = "muscleGroup") String muscleGroupName,
            @RequestParam(name = "muscleName") String muscleName,
            @RequestParam(name = "muscle_info") String info,
            @RequestParam(name = "groupId") Long groupId,
            @RequestParam(name = "file") MultipartFile image
    ) {
        try {
            muscleService.addMuscle(muscleGroupName, muscleName, info, image);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
        return "redirect:/muscleGroups/" + groupId;
    }

    //TODO add delete image
    @PostMapping("/{id}/delete")
    public String deleteMuscleById(@PathVariable long id,
            @RequestParam(name = "groupId") Long groupId) {
        try {
            muscleService.deleteMuscleById(id);
        } catch (CanNotDeleteException e) {
            System.out.println("OSIBKA");//TODO make smth with this
            e.printStackTrace();
        }
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
