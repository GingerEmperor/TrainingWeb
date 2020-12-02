package org.example.controllers;

import java.util.ArrayList;
import java.util.List;

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
    public String showAll(Model model) {
        model.addAttribute("allTrainings", trainingService.findAll());
        model.addAttribute("allMuscleGroups", muscleGroupService.findAll());
        return "trainingTemplates/trainings";
    }

    @GetMapping("/add")
    public String addTrainingPage(
            @RequestParam(name = "muscleGroups") String stringWithMuscleGroupIds,
            Model model
    ) {
        //Add muscles
        List<MuscleGroup> muscleGroups = new ArrayList<>();
        List<Muscle> muscles = new ArrayList<>();
        List<Exercise> exercises = new ArrayList<>();

        String[] arrOfMuscleGroupId = stringWithMuscleGroupIds.split(",");

        for (String muscleGroupId : arrOfMuscleGroupId) {
            MuscleGroup muscleGroup = muscleGroupService.findById(Long.parseLong(muscleGroupId));
            muscleGroups.add(muscleGroup);
            muscles.addAll(muscleGroup.getMuscleSet());
        }

        //add exercises
        for (Muscle muscle : muscles) {
            Exercise separator = new Exercise();
            separator.setTitle("---" + muscle.getName() + "---");
            exercises.add(separator);
            exercises.addAll(exerciseService.findAllByPrimaryWorkingMuscle(muscle));
        }

        model.addAttribute("muscleGroups", muscleGroups);
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
            @RequestParam(name = "muscleGroups") String mainGroups,
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
        System.out.println(mainGroups);
        //TODO to not save trainings with same title

        final String[] exercisesNameArr = exerciseName.split(",");
        final String[] howMuchToDoArr = howMuchToDo.split(",");
        final String[] recommendedTimeToDoArr = recommendedTimeToDo.split(",");
        final String[] trailsArr = trailsCount.split(",");
        final String[] timeToRestArr = timeToRest.split(",");

        final String mainGroupsIndexesString = mainGroups.substring(1, mainGroups.length() - 1);
        final String[] mainGroupsNamesArr = mainGroupsIndexesString.split(",");

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

        List<MuscleGroup> muscleGroups = new ArrayList<>();
        for (int i = 0; i < mainGroupsNamesArr.length; i++) {
            muscleGroups.add(muscleGroupService.
                    findByName(mainGroupsNamesArr[i].trim()));
        }
        training.setMuscleGroups(muscleGroups);

        //TODO to add image

        System.out.println(trainingService.save(training));

        return "redirect:/trainings";
    }

    @GetMapping("/{id}")
    public String showTrainingDetails(@PathVariable Long id, Model model) {

        return "trainingTemplates/trainingDetails";
    }

}
