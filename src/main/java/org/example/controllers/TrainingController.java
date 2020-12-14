package org.example.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.models.Exercise;
import org.example.models.Training;
import org.example.models.TrainingElement;
import org.example.models.enums.Difficulty;
import org.example.models.enums.ForWho;
import org.example.models.enums.Goal;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.services.ExerciseService;
import org.example.services.MuscleGroupService;
import org.example.services.TrainingElementService;
import org.example.services.TrainingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    private final MuscleGroupService muscleGroupService;

    private final ExerciseService exerciseService;

    private final TrainingElementService trainingElementService;

    @GetMapping()
    public String showAllByAlphabeticalOrder(Model model) {
        List<Training> trainings = trainingService.findAll();
        Collections.sort(trainings,(t1, t2) -> t1.getName().toLowerCase().compareTo(t2.getName().toLowerCase()));
        model.addAttribute("allTrainings", trainings);
        model.addAttribute("allMuscleGroups", muscleGroupService.findAll());
        return "trainingTemplates/trainings";
    }

    @GetMapping("/all")
    public String showAll(Model model) {
        model.addAttribute("allTrainings", trainingService.findAll());
        model.addAttribute("allMuscleGroups", muscleGroupService.findAll());
        return "trainingTemplates/trainings";
    }

    @GetMapping("/add")
    public String addTrainingPage(
            @RequestParam(name = "primaryMuscleGroup") String stringWithPrimaryMuscleGroupIds,
            @RequestParam(name = "secondaryMuscleGroup") String stringWithSecondaryMuscleGroupIds,
            Model model
    ) {
        //Add muscles
        List<MuscleGroup> primaryMuscleGroup = new ArrayList<>();
        List<MuscleGroup> secondaryMuscleGroup = new ArrayList<>();
        List<Muscle> muscles = new ArrayList<>();
        List<Exercise> exercises = new ArrayList<>();

        String[] arrOfPrimaryMuscleGroupId = stringWithPrimaryMuscleGroupIds.split(",");
        String[] arrOfSecondaryMuscleGroupId = stringWithSecondaryMuscleGroupIds.split(",");

        for (String muscleGroupId : arrOfPrimaryMuscleGroupId) {
            MuscleGroup muscleGroup = muscleGroupService.findById(Long.parseLong(muscleGroupId));
            primaryMuscleGroup.add(muscleGroup);
            muscles.addAll(muscleGroup.getMuscleSet());
        }

        for (String muscleGroupId : arrOfSecondaryMuscleGroupId) {
            MuscleGroup muscleGroup = muscleGroupService.findById(Long.parseLong(muscleGroupId));
            secondaryMuscleGroup.add(muscleGroup);
            muscles.addAll(muscleGroup.getMuscleSet());
        }

        //add exercises
        for (Muscle muscle : muscles) {
            Exercise separator = new Exercise();
            separator.setTitle("---" + muscle.getName() + "---");
            exercises.add(separator);
            exercises.addAll(exerciseService.findAllByPrimaryWorkingMuscle(muscle));
        }

        model.addAttribute("primaryMuscleGroup", primaryMuscleGroup);
        model.addAttribute("secondaryMuscleGroup", secondaryMuscleGroup);
        model.addAttribute("muscles", muscles);
        model.addAttribute("exercises", exercises);
        model.addAttribute("forWhos", ForWho.values());
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("goals", Goal.values());
        return "trainingTemplates/addTrainingPage";
    }

    @PostMapping()
    public String addTraining(
            @RequestParam(name = "trainingName") String trainingName,
            @RequestParam(name = "primaryMuscleGroup") String mainMuscleGroups,
            @RequestParam(name = "secondaryMuscleGroup",required = false) String secondaryMuscleGroups,
            @RequestParam(name = "forWho") String forWho,
            @RequestParam(name = "difficulty") String difficulty,
            @RequestParam(name = "goal") String goal,
            @RequestParam(name = "exercise") String exerciseName,
            @RequestParam(name = "howMuch") String howMuchToDo,
            @RequestParam(name = "recommendedTimeToDo") String recommendedTimeToDo,
            @RequestParam(name = "trial") String trailsCount,
            @RequestParam(name = "rest") String timeToRest,
            @RequestParam(name = "someAdvice") String someAdvice
    ) {
        System.out.println(mainMuscleGroups);
        //TODO to not save trainings with same title

        trainingService.checkIfExistsTrainingByName(trainingName);

        final String[] exercisesNameArr = exerciseName.split(",");
        final String[] howMuchToDoArr = howMuchToDo.split(",");
        final String[] recommendedTimeToDoArr = recommendedTimeToDo.split(",");
        final String[] trailsArr = trailsCount.split(",");
        final String[] timeToRestArr = timeToRest.split(",");

        final String mainGroupsIndexesString = mainMuscleGroups.substring(1, mainMuscleGroups.length() - 1);
        final String[] mainGroupsNamesArr = mainGroupsIndexesString.split(",");

        final String secondaryGroupsIndexesString = secondaryMuscleGroups.substring(1, secondaryMuscleGroups.length() - 1);
        final String[] secondaryGroupsNamesArr = secondaryGroupsIndexesString.split(",");

        List<TrainingElement> trainingElements = new ArrayList<>();
        System.out.println(mainGroupsIndexesString);
        for (String s : mainGroupsNamesArr) {
            System.out.println(s);
        }

        for (int i = 0; i < exercisesNameArr.length; i++) {
            for (int j = 0; j < Integer.parseInt(trailsArr[i]); j++) {
                TrainingElement trainingElement = trainingElementService.getOrMake(
                        exerciseService.findByTitle(exercisesNameArr[i]),
                        Integer.parseInt(howMuchToDoArr[i]),
                        Integer.parseInt(recommendedTimeToDoArr[i]),
                        Integer.parseInt(timeToRestArr[i])
                );

                trainingElements.add(trainingElementService.save(trainingElement));
            }
        }

        Training training = Training.builder()
                .name(trainingName)
                .forWho(ForWho.valueOf(forWho))
                .difficulty(Difficulty.valueOf(difficulty))
                .goal(Goal.valueOf(goal))
                .trainingElements(trainingElements)
                .advice(someAdvice)
                .build();

        List<MuscleGroup> primaryMuscleGroups = new ArrayList<>();
        for (int i = 0; i < mainGroupsNamesArr.length; i++) {
            primaryMuscleGroups.add(muscleGroupService.
                    findByName(mainGroupsNamesArr[i].trim()));
        }

        List<MuscleGroup> secondaryMuscleGroupsList = new ArrayList<>();
        for (int i = 0; i < secondaryGroupsNamesArr.length; i++) {
            secondaryMuscleGroupsList.add(muscleGroupService.
                    findByName(secondaryGroupsNamesArr[i].trim()));
        }
        training.setPrimaryMuscleGroups(primaryMuscleGroups);
        training.setSecondaryMuscleGroups(secondaryMuscleGroupsList);

        //TODO to add image

        System.out.println(trainingService.save(training));

        return "redirect:/trainings";
    }

    @GetMapping("/{id}")
    public String showTrainingDetails(@PathVariable Long id, Model model) {
        Training training = trainingService.findById(id);
        model.addAttribute("training", training);

        List<TrainingElement> trainingElements=training.getTrainingElements();
        final Set<Exercise> exerciseSet = new LinkedHashSet<>();
        trainingElements.forEach(trainingElement -> exerciseSet.add(trainingElement.getExercise()));
                // trainingElements.stream().map(TrainingElement::getExercise).collect(Collectors.toSet());

        model.addAttribute("exerciseSet",exerciseSet);
        return "trainingTemplates/trainingDetails";
    }

}
