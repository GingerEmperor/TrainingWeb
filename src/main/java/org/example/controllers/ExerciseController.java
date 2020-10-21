package org.example.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.example.exeptions.FileCanNotSaveException;
import org.example.models.Exercise;
import org.example.models.enums.Equipment;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.services.ExerciseService;
import org.example.services.GlobalService;
import org.example.services.MuscleGroupService;
import org.example.services.MuscleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Controller
@Data
@RequestMapping("/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    private final MuscleService muscleService;

    private final MuscleGroupService muscleGroupService;

    private final GlobalService globalService;

    @Value("${upload.exercisePath}")
    private String uploadPath;

    @GetMapping()
    public String showAll(Model model) {
        Iterable<Exercise> allExercises = exerciseService.findAll();
        model.addAttribute("allExercises", allExercises);

        return "exercises";
    }

    @GetMapping("/add")
    public String addExerciseForm(Model model) {
        Iterable<MuscleGroup> allMuscleGroups = muscleGroupService.findAll();
        Iterable<Equipment> allEquipment = Arrays.asList(Equipment.values().clone());

        model.addAttribute("allMuscleGroups", allMuscleGroups);
        model.addAttribute("allEquipment", allEquipment);
        return "addExerciseFormPart1";
    }

    @PostMapping("/add/attributes")
    public String addExercisesTest(
            @RequestParam(required = false, name = "exerciseTitle") String exerciseTitle,
            @RequestParam(required = false, name = "primaryMuscleGroup") String primaryMuscleIdArr,
            @RequestParam(required = false, name = "secondaryMuscleGroup") String secondMuscleIdArr,
            @RequestParam(required = false, name = "equipment") Equipment equipment,
            Model model) {

        List<MuscleGroup> primaryMuscleGroups = new ArrayList<>();
        List<MuscleGroup> secondaryMuscleGroups = new ArrayList<>();

        String[] primIds = primaryMuscleIdArr.split(",");
        String[] secIds = secondMuscleIdArr.split(",");
        for (int i = 0; i < primIds.length; i++) {
            primaryMuscleGroups.add(muscleGroupService.findById(Long.parseLong(primIds[i])));
        }
        List<Muscle> primaryMuscles = new ArrayList<>();
        for (MuscleGroup mG : primaryMuscleGroups) {
            primaryMuscles.addAll(mG.getMuscleSet());
        }

        for (int i = 0; i < secIds.length; i++) {
            secondaryMuscleGroups.add(muscleGroupService.findById(Long.parseLong(secIds[i])));
        }
        List<Muscle> secondaryMuscles = new ArrayList<>();
        for (MuscleGroup mG : secondaryMuscleGroups) {
            secondaryMuscles.addAll(mG.getMuscleSet());
        }

        model.addAttribute("exerciseTitle", exerciseTitle);
        model.addAttribute("equip", equipment);
        model.addAttribute("primaryMuscles", primaryMuscles);
        model.addAttribute("secondaryMuscles", secondaryMuscles);

        return "addExerciseFormPart2";
    }

    @PostMapping("/add")
    public String addExercisesToDB(
            @RequestParam(name = "exerciseTitle") String exerciseTitle,
            @RequestParam(name = "primaryMuscles") String primaryMuscles,
            @RequestParam(name = "secondaryMuscles") String secondaryMuscles,
            @RequestParam(name = "equipment") Equipment equipment,
            @RequestParam(name = "exerciseInfo") String exerciseInfo,
            @RequestParam(name = "previewImg") MultipartFile previewImg
    ) throws IOException {


        Exercise currentExercise=exerciseService.findByTitle(exerciseTitle);
        if (currentExercise != null) {
            throw new RuntimeException("Такое упраднение уже есть");
        }
        Set<Muscle> primaryMuscleSet = new HashSet<>();
        Set<Muscle> secondaryMuscleSet = new HashSet<>();

        String[] primIds = primaryMuscles.split(",");
        String[] secIds = secondaryMuscles.split(",");
        for (int i = 0; i < primIds.length; i++) {
            primaryMuscleSet.add(muscleService.findById(Long.parseLong(primIds[i])));
        }

        for (int i = 0; i < secIds.length; i++) {
            secondaryMuscleSet.add(muscleService.findById(Long.parseLong(secIds[i])));
        }

        try {
            currentExercise = exerciseService.createNewExercise(
                    exerciseTitle, primaryMuscleSet, secondaryMuscleSet,
                    exerciseInfo, equipment,
                    globalService.saveImgToPathWithPrefixName(previewImg,uploadPath,exerciseTitle));
        }catch (FileCanNotSaveException e){
            currentExercise = exerciseService.createNewExercise(
                    exerciseTitle, primaryMuscleSet, secondaryMuscleSet,
                    exerciseInfo, equipment);
        }
        exerciseService.save(currentExercise);
        return "redirect:/exercises";

    }

}
