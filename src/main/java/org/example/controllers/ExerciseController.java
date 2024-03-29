package org.example.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.lang.String.format;

import org.example.exeptions.AlreadyExistsException;
import org.example.exeptions.SearchFailException;
import org.example.models.Exercise;
import org.example.models.criteria.Crit;
import org.example.models.criteria.EquipmentCriteria;
import org.example.models.enums.Equipment;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.services.ExerciseService;
import org.example.services.GlobalService;
import org.example.services.MuscleGroupService;
import org.example.services.MuscleService;
import org.example.utill.alerts.DangerAlert;
import org.example.utill.alerts.InfoAlert;
import org.example.utill.alerts.SuccessAlert;
import org.example.utill.alerts.WarningAlert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import static org.example.services.GlobalService.alert;
import static org.example.utill.StringsFormatsUtils.EXERCISE_WAS_ADDED_FORMAT;
import static org.example.utill.StringsFormatsUtils.EXERCISE_WAS_DELETED_FORMAT;
import static org.example.utill.StringsFormatsUtils.EXERCISE_WAS_UPDATED_FORMAT;

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

        Map<String, List<Exercise>> criteria_exerciseMap = new TreeMap<>();

        List<Exercise> allExercises = new ArrayList<>(exerciseService.findAll());
        Collections.sort(allExercises, (o1, o2) -> o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase()));
        criteria_exerciseMap.put("All", allExercises);

        model.addAttribute("sortCriteria_ExerciseMap", criteria_exerciseMap);
        model.addAttribute("alert", alert.poll());

        return "exerciseTemplates/exercises";
    }

    @GetMapping("/byMuscleGroups")
    public String showAllByMuscleGroups(Model model) {

        Map<MuscleGroup, List<Exercise>> muscleGroup_ExerciseMap = new LinkedHashMap<>();
        for (MuscleGroup mG : muscleGroupService.findAll()) {
            List<Exercise> exercisesByMuscleGroup = new ArrayList<>(exerciseService.findAllByPrimaryWorkingMuscleGroup(mG));
            exercisesByMuscleGroup.sort((o1, o2) -> o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase()));
            muscleGroup_ExerciseMap.put(mG, exercisesByMuscleGroup);
        }

        model.addAttribute("sortCriteria_ExerciseMap", muscleGroup_ExerciseMap);
        return "exerciseTemplates/exercises";

    }

    @GetMapping("/byMuscleGroups/{id}")
    public String showAllByConcreteMuscleGroup(
            @PathVariable(name = "id") long id,
            Model model) {

        Map<MuscleGroup, List<Exercise>> muscleGroupExerciseMap = new LinkedHashMap<>();
        MuscleGroup foundedMuscleGroup = muscleGroupService.findById(id);
        List<Exercise> exercisesByMuscleGroup = new ArrayList<>(exerciseService.findAllByPrimaryWorkingMuscleGroup(foundedMuscleGroup));
        exercisesByMuscleGroup.sort((o1, o2) -> o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase()));
        muscleGroupExerciseMap.put(foundedMuscleGroup, exercisesByMuscleGroup);

        model.addAttribute("sortCriteria_ExerciseMap", muscleGroupExerciseMap);
        return "exerciseTemplates/exercises";

    }

    @GetMapping("/byMuscle/{id}")
    public String showAllByConcreteMuscle(
            @PathVariable(name = "id") long id,
            Model model) {

        Map<Muscle, List<Exercise>> muscle_ExerciseMap = new LinkedHashMap<>();
        Muscle muscle = muscleService.findById(id);
        List<Exercise> exercisesByMuscle = new ArrayList<>(exerciseService.findAllByPrimaryWorkingMuscle(muscle));
        exercisesByMuscle.sort((o1, o2) -> o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase()));
        muscle_ExerciseMap.put(muscle, exercisesByMuscle);

        model.addAttribute("sortCriteria_ExerciseMap", muscle_ExerciseMap);
        return "exerciseTemplates/exercises";

    }

    @GetMapping("/byEquipmentNeed")
    public String showAllByEquipmentNeed(Model model) {

        Map<Crit, List<Exercise>> equipment_ExerciseMap = new LinkedHashMap<>();
        for (Equipment eq : Equipment.values()) {
            List<Exercise> exercisesByMuscleGroup = new ArrayList<>(exerciseService.findAllByEquipment(eq));
            Collections.sort(exercisesByMuscleGroup, (o1, o2) -> o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase()));
            equipment_ExerciseMap.put(new EquipmentCriteria(eq), exercisesByMuscleGroup);
        }

        model.addAttribute("sortCriteria_ExerciseMap", equipment_ExerciseMap);

        return "exerciseTemplates/exercises";
    }

    @GetMapping("/all")//TODO for debug
    public String showAll(Model model) {
        Map<String, List<Exercise>> muscleGroup_ExerciseMap = new TreeMap<>();
        List<Exercise> allExercises = new ArrayList<>(exerciseService.findAll());
        muscleGroup_ExerciseMap.put("All", allExercises);

        model.addAttribute("sortCriteria_ExerciseMap", muscleGroup_ExerciseMap);

        return "exerciseTemplates/exercises";
    }

    @GetMapping("/{id}")
    public String showExerciseInfoById(@PathVariable(name = "id") long id,
            Model model) {
        try {
            model.addAttribute("exercise", exerciseService.findById(id));
            return "exerciseTemplates/exerciseDetails";
        }catch (Exception e){
            alert.add(new DangerAlert(e.getMessage()));
            return "redirect:/exercises";
        }
    }

    @GetMapping("/searchBy")
    public String searchBy(@RequestParam(name = "search_by") String searchBy,
            @RequestParam(name = "inputSearch", required = false) String inputSearch
    ) {
        try {
            switch (searchBy) {
                case "muscleGroup":
                    System.out.println("muscleGroup");
                    try {
                        MuscleGroup muscleGroup = muscleGroupService.findByName(inputSearch);
                        return "redirect:byMuscleGroups/" + muscleGroup.getId();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new SearchFailException("Невозилжно найти такую группу мышц");
                    }
                case "muscle":
                    System.out.println("muscle");//
                    try {
                        Muscle muscle = muscleService.findByName(inputSearch);
                        return "redirect:byMuscle/" + muscle.getId();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new SearchFailException("Невозилжно найти такую мышцу");
                    }
                default:
                    System.out.println("exerciseTile");
                    try {
                        Exercise exercise = exerciseService.findByTitle(inputSearch);
                        return "redirect:" + exercise.getId();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new SearchFailException("Невозилжно найти такое упражнение");
                    }
            }
        } catch (SearchFailException e) {
            e.printStackTrace();
            //add ничкго не нацдено
            return "redirect:/exercises";
        }
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

        try {
            exerciseService.checkIfExistsExerciseByTitle(exerciseTitle);
        } catch (AlreadyExistsException e) {
            e.printStackTrace();
            alert.add(new DangerAlert(e.getMessage()));
            return "redirect:/exercises";
        }

        List<MuscleGroup> primaryMuscleGroups = new ArrayList<>();
        List<MuscleGroup> secondaryMuscleGroups = new ArrayList<>();

        String[] primIds = primaryMuscleIdArr.split(",");
        for (final String primId : primIds) {
            primaryMuscleGroups.add(muscleGroupService.findById(Long.parseLong(primId)));
        }
        List<Muscle> primaryMuscles = new ArrayList<>();
        for (MuscleGroup mG : primaryMuscleGroups) {
            Muscle separator = new Muscle();
            separator.setName("---" + mG.getName() + "---");
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
            Muscle separator = new Muscle();
            separator.setName("---" + mG.getName() + "---");
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
            @RequestParam(name = "someInfo") String exerciseInfo,
            @RequestParam(name = "howToDo") String howToDo,
            @RequestParam(name = "videoLink") String videoLink,
            @RequestParam(name = "previewImg") MultipartFile previewImg,
            @RequestParam(name = "img1") MultipartFile startImg,
            @RequestParam(name = "img2") MultipartFile finishImg
    ) {
        // Equipment equipment=Equipment.;
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
            globalService.checkIfNameIsValid(exerciseTitle);
            exerciseService.createNewExercise(exerciseTitle, primaryMuscleSet,
                    secondaryMuscleSet, exerciseInfo, howToDo, videoLink,
                    equipment, previewImg, startImg, finishImg);
            alert.add(new SuccessAlert(format(EXERCISE_WAS_ADDED_FORMAT, exerciseTitle)));
            return "redirect:/exercises";
        } catch (Exception e) {
            e.printStackTrace();
            alert.add(new DangerAlert(e.getMessage()));
            return "redirect:/";
        }
    }

    @PatchMapping("/{id}/edit")
    public String editAttributesExercise(@PathVariable long id, Model model) {
        Iterable<MuscleGroup> allMuscleGroups = muscleGroupService.findAll();
        Iterable<Equipment> allEquipment = Arrays.asList(Equipment.values().clone());
        Exercise editExercise = exerciseService.findById(id);

        model.addAttribute("allMuscleGroups", allMuscleGroups);
        model.addAttribute("allEquipment", allEquipment);
        model.addAttribute("editExercise", editExercise);
        return "exerciseTemplates/editExerciseFormPart1";
    }

    @PatchMapping("/{id}/edit/attributes")
    public String editExercise(
            @RequestParam(required = true, name = "exerciseTitle") String exerciseTitle,
            @RequestParam(required = true, name = "primaryMuscleGroup") String primaryMuscleIdArr,
            @RequestParam(required = false, name = "secondaryMuscleGroup") String secondMuscleIdArr,
            @RequestParam(required = true, name = "equipment") Equipment equipment,
            @RequestParam(required = true, name = "editExerciseId") Long editExerciseId,
            Model model) {

        try {
            Exercise editExercise = exerciseService.findById(editExerciseId);
            if (editExercise != null && !editExercise.getTitle().equals(exerciseTitle)) {
                exerciseService.checkIfExistsExerciseByTitle(exerciseTitle);
            }

            List<MuscleGroup> primaryMuscleGroups = new ArrayList<>();
            List<MuscleGroup> secondaryMuscleGroups = new ArrayList<>();

            String[] primIds = primaryMuscleIdArr.split(",");
            for (final String primId : primIds) {
                primaryMuscleGroups.add(muscleGroupService.findById(Long.parseLong(primId)));
            }
            List<Muscle> primaryMuscles = new ArrayList<>();
            for (MuscleGroup mG : primaryMuscleGroups) {
                Muscle separator = new Muscle();
                separator.setName("---" + mG.getName() + "---");
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
                Muscle separator = new Muscle();
                separator.setName("---" + mG.getName() + "---");
                secondaryMuscles.add(separator);

                secondaryMuscles.addAll(mG.getMuscleSet());
            }

            model.addAttribute("editExercise", editExercise);
            model.addAttribute("exerciseTitle", exerciseTitle);
            model.addAttribute("equip", equipment);
            model.addAttribute("primaryMuscles", primaryMuscles);
            model.addAttribute("secondaryMuscles", secondaryMuscles);

            return "exerciseTemplates/editExerciseFormPart2";
        }catch (Exception e){
            alert.add(new DangerAlert(e.getMessage()));
            return "redirect:/exercises";
        }
    }

    @PatchMapping("/edit")
    public String editExercisesInDB(
            @RequestParam(name = "editExerciseId") Long editExerciseId,
            @RequestParam(name = "exerciseTitle") String exerciseTitle,
            @RequestParam(name = "primaryMuscles") String primaryMusclesIdArr,
            @RequestParam(name = "secondaryMuscles", required = false) String secondaryMusclesIdArr,
            @RequestParam(name = "equipment") Equipment equipment,
            @RequestParam(name = "someInfo") String exerciseInfo,
            @RequestParam(name = "howToDo") String howToDo,
            @RequestParam(name = "videoLink") String videoLink,
            @RequestParam(name = "previewImg") MultipartFile previewImg
    ) {
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
            globalService.checkIfNameIsValid(exerciseTitle);
            exerciseService.updateExercise(editExerciseId, exerciseTitle, primaryMuscleSet, secondaryMuscleSet, exerciseInfo, howToDo, videoLink, equipment, previewImg);
            alert.add(new InfoAlert(format(EXERCISE_WAS_UPDATED_FORMAT, exerciseTitle)));
            return "redirect:/exercises";
        } catch (Exception e) {
            e.printStackTrace();
            alert.add(new DangerAlert(e.getMessage()));
            return "redirect:/exercises";
        }
    }

    @DeleteMapping("/{id}/delete")
    public String deleteMuscleById(@PathVariable long id) {
        try {
            // if ()
            // if(exerciseService.findById(id))
            // exerciseService.
            final Exercise exerciseToDelete = exerciseService.findById(id);
            exerciseService.deleteExerciseById(id);
            alert.add(new WarningAlert(format(EXERCISE_WAS_DELETED_FORMAT, exerciseToDelete.getTitle())));
            return "redirect:/exercises";
        } catch (Exception e) {
            e.printStackTrace();
            alert.add(new DangerAlert(e.getMessage()));
            return "redirect:/exercises";
        }
    }

}
