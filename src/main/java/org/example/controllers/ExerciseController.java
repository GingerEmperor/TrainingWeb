package org.example.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public String showAllByAlphabeticalOrder(Model model){
        List<Exercise> allExercises=exerciseService.findAll();
        Collections.sort(allExercises, Comparator.comparing(Exercise::getTitle));//sort alphabetical
        model.addAttribute("allExercises",allExercises);
        return "exercises";
    }

    @GetMapping("/byEquipmentNeed")//TODO to add frontend link for this
    public String showAllByEquipmentNeed(Model model){
        List<Exercise> allExercises=exerciseService.findAll();
        Collections.sort(allExercises, Comparator.comparing(Exercise::getEquipmentNeed));//sort alphabetical
        // Collections.sort(allExercises, (o1, o2) -> );//sort alphabetical
        model.addAttribute("allExercises",allExercises);
        return "exercises";
    }

    @GetMapping("/all")//TODO for debug
    public String showAll(Model model){
        Iterable<Exercise> allExercises=exerciseService.findAll();
        model.addAttribute("allExercises",allExercises);
        return "exercises";
    }



    @GetMapping("/byMuscleGroups")//TODO to add frontend link for this
    public String showAllByMuscleGroups(Model model) {

        //TODO maybe use Map<MuscleGroup,Set<Exercises>>
        Map<MuscleGroup, Set<Exercise>> muscleGroupExerciseMap = new HashMap<>();
        MuscleGroup muscleGroup = new MuscleGroup();
        for (MuscleGroup mG : muscleGroupService.findAll()) {
            Set<Exercise> exercisesByMuscleGroup = exerciseService.findAllByPrimaryWorkingMuscleGroup(mG);
            muscleGroupExerciseMap.put(mG, exercisesByMuscleGroup);
        }

        muscleGroupExerciseMap.keySet().forEach(muscleGroup1 -> System.out.println(muscleGroup1.getName()));
        System.out.println("================");
        for (Set<Exercise> e : muscleGroupExerciseMap.values()) {
            e.stream().forEach(exercise -> System.out.println(exercise.getTitle()));
            System.out.println("-----");
        }
        return null;
        //
        // Iterable<Exercise> allExercises = exerciseService.findAll();
        // model.addAttribute("allExercises", allExercises);

        // return "exercises";
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
            @RequestParam(required = true, name = "exerciseTitle") String exerciseTitle,
            @RequestParam(required = true, name = "primaryMuscleGroup") String primaryMuscleIdArr,
            @RequestParam(required = false, name = "secondaryMuscleGroup") String secondMuscleIdArr,
            @RequestParam(required = true, name = "equipment") Equipment equipment,
            Model model) {

        List<MuscleGroup> primaryMuscleGroups = new ArrayList<>();
        List<MuscleGroup> secondaryMuscleGroups = new ArrayList<>();

        String[] primIds = primaryMuscleIdArr.split(",");
        for (final String primId : primIds) {
            primaryMuscleGroups.add(muscleGroupService.findById(Long.parseLong(primId)));
        }
        List<Muscle> primaryMuscles = new ArrayList<>();
        for (MuscleGroup mG : primaryMuscleGroups) {
            primaryMuscles.addAll(mG.getMuscleSet());
        }

        if (secondMuscleIdArr != null) {
            String[] secIds = secondMuscleIdArr.split(",");
            for (final String secId : secIds) {
                secondaryMuscleGroups.add(muscleGroupService.findById(Long.parseLong(secId)));
            }
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
            @RequestParam(name = "primaryMuscles") String primaryMusclesIdArr,
            @RequestParam(name = "secondaryMuscles", required = false) String secondaryMusclesIdArr,
            @RequestParam(name = "equipment") Equipment equipment,
            @RequestParam(name = "exerciseInfo") String exerciseInfo,
            @RequestParam(name = "previewImg") MultipartFile previewImg
    ) throws IOException {

        Exercise currentExercise = exerciseService.findByTitle(exerciseTitle);
        if (currentExercise != null) {
            throw new RuntimeException("Такое упраднение уже есть");
        }
        Set<Muscle> primaryMuscleSet = new HashSet<>();
        Set<Muscle> secondaryMuscleSet = new HashSet<>();

        String[] primIds = primaryMusclesIdArr.split(",");
        for (final String primId : primIds) {
            primaryMuscleSet.add(muscleService.findById(Long.parseLong(primId)));
        }

        if (secondaryMusclesIdArr != null) {
            String[] secIds = secondaryMusclesIdArr.split(",");
            for (final String secId : secIds) {
                secondaryMuscleSet.add(muscleService.findById(Long.parseLong(secId)));
            }
        }

        try {
            currentExercise = exerciseService.createNewExercise(
                    exerciseTitle, primaryMuscleSet, secondaryMuscleSet,
                    exerciseInfo, equipment,
                    globalService.saveImgToPathWithPrefixName(previewImg, uploadPath, exerciseTitle));
        } catch (FileCanNotSaveException e) {
            currentExercise = exerciseService.createNewExercise(
                    exerciseTitle, primaryMuscleSet, secondaryMuscleSet,
                    exerciseInfo, equipment);
        }
        exerciseService.save(currentExercise);
        return "redirect:/exercises";

    }

}
