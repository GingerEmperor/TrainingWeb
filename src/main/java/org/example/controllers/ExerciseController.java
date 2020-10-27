package org.example.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import org.springframework.web.bind.annotation.PathVariable;
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
    public String showAllByAlphabeticalOrder(Model model) {

        Map<String, List<Exercise>> muscleGroup_ExerciseMap = new TreeMap<>();

        List<Exercise> allExercises =new ArrayList<>(exerciseService.findAll());
        Collections.sort(allExercises,(o1, o2) -> o1.getTitle().toLowerCase().charAt(0)-o2.getTitle().toLowerCase().charAt(0));
        muscleGroup_ExerciseMap.put("All", allExercises);

        model.addAttribute("sortCriteria_ExerciseMap", muscleGroup_ExerciseMap);

        return "exerciseTemplates/exercises";
    }

    @GetMapping("/byMuscleGroups")//TODO to add frontend link for this
    public String showAllByMuscleGroups(Model model) {

        //TODO maybe use Map<MuscleGroup,Set<Exercises>>
        Map<MuscleGroup, Set<Exercise>> muscleGroup_ExerciseMap = new TreeMap<>();
        for (MuscleGroup mG : muscleGroupService.findAll()) {
            Set<Exercise> exercisesByMuscleGroup = exerciseService.findAllByPrimaryWorkingMuscleGroup(mG);
            muscleGroup_ExerciseMap.put(mG, exercisesByMuscleGroup);
        }

        model.addAttribute("sortCriteria_ExerciseMap", muscleGroup_ExerciseMap);
        return "exerciseTemplates/exercises";

    }

    @GetMapping("/byEquipmentNeed")
    public String showAllByEquipmentNeed(Model model) {

        Map<Equipment, List<Exercise>> equipment_ExerciseMap = new TreeMap<>();
        for (Equipment eq : Equipment.values()) {
            List<Exercise> exercisesByMuscleGroup = new ArrayList<>(exerciseService.findAllByEquipment(eq));
            Collections.sort(exercisesByMuscleGroup,(o1, o2) -> o1.getTitle().toLowerCase().charAt(0)-o2.getTitle().toLowerCase().charAt(0));
            equipment_ExerciseMap.put(eq, exercisesByMuscleGroup);
        }

        model.addAttribute("sortCriteria_ExerciseMap", equipment_ExerciseMap);

        return "exerciseTemplates/exercises";
    }

    @GetMapping("/all")//TODO for debug
    public String showAll(Model model) {
        Iterable<Exercise> allExercises = exerciseService.findAll();
        model.addAttribute("allExercises", allExercises);
        return "exerciseTemplates/exercises";
    }

    @GetMapping("/{id}")
    public String showExerciseInfoById(@PathVariable(name = "id") long id,
            Model model) {
        System.out.println("Works1");
        model.addAttribute("exercise", exerciseService.findById(id));
        System.out.println("Works2");
        return "exerciseTemplates/exerciseDetails";
    }

    @GetMapping("/add")
    public String addExerciseForm(Model model) {
        Iterable<MuscleGroup> allMuscleGroups = muscleGroupService.findAll();
        Iterable<Equipment> allEquipment = Arrays.asList(Equipment.values().clone());

        model.addAttribute("allMuscleGroups", allMuscleGroups);
        model.addAttribute("allEquipment", allEquipment);
        return "exerciseTemplates/addExerciseFormPart1";
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
            Muscle separator=new Muscle();
            separator.setName("---"+ mG.getName()+"---");
            primaryMuscles.add(separator);

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
            Muscle separator=new Muscle();
            separator.setName("---"+ mG.getName()+"---");
            secondaryMuscles.add(separator);

            secondaryMuscles.addAll(mG.getMuscleSet());
        }

        model.addAttribute("exerciseTitle", exerciseTitle);
        model.addAttribute("equip", equipment);
        model.addAttribute("primaryMuscles", primaryMuscles);
        model.addAttribute("secondaryMuscles", secondaryMuscles);

        return "exerciseTemplates/addExerciseFormPart2";
    }

    @PostMapping("/add")
    public String addExercisesToDB(
            @RequestParam(name = "exerciseTitle") String exerciseTitle,
            @RequestParam(name = "primaryMuscles") String primaryMusclesIdArr,
            @RequestParam(name = "secondaryMuscles", required = false) String secondaryMusclesIdArr,
            @RequestParam(name = "equipment") Equipment equipment,
            @RequestParam(name = "someInfo") String someInfo,
            @RequestParam(name = "howToDo") String howToDo,
            @RequestParam(name = "videoLink") String videoLink,
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
                    someInfo,howToDo,videoLink, equipment,
                    globalService.saveImgToPathWithPrefixName(previewImg, uploadPath, exerciseTitle));
        } catch (FileCanNotSaveException e) {
            currentExercise = exerciseService.createNewExercise(
                    exerciseTitle, primaryMuscleSet, secondaryMuscleSet,
                    someInfo,howToDo,videoLink, equipment);
        }
        exerciseService.save(currentExercise);
        return "redirect:/exercises";

    }

    // //TODO add delete image
    // @PostMapping("/{id}/delete")
    // public String deleteMuscleById(@PathVariable long id,
    //         @RequestParam(name = "groupId") Long groupId) {
    //     muscleService.deleteMuscleById(id);
    //     return "redirect:/muscleGroups/" + groupId;
    // }

}
