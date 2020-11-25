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
import org.example.services.ExerciseService;
import org.example.services.MuscleGroupService;
import org.example.services.TrainingElementService;
import org.example.services.TrainingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        List<Muscle> muscles = new ArrayList<>();
        List<Exercise> exercises = new ArrayList<>();

        String[] arrOfMuscleGroupId = stringWithMuscleGroupIds.split(",");

        for (String muscleGroupId : arrOfMuscleGroupId) {
            muscles.addAll(muscleGroupService.findById(Long.parseLong(muscleGroupId))
                    .getMuscleSet());
        }

        //add exercises
        for (Muscle muscle : muscles) {
            Exercise separator = new Exercise();
            separator.setTitle("---" + muscle.getName() + "---");
            exercises.add(separator);
            exercises.addAll(exerciseService.findAllByPrimaryWorkingMuscle(muscle));
        }

        model.addAttribute("muscles", muscles);
        model.addAttribute("exercises", exercises);
        model.addAttribute("forWhos", ForWho.values());
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("goals", Goal.values());
        return "trainingTemplates/addTrainingPage";
    }

    @PostMapping()
    public String addTraining(
            @RequestParam(name = "trainingName", required = false) String trainingName,
            @RequestParam(name = "forWho", required = false) String forWho,
            @RequestParam(name = "difficulty", required = false) String difficulty,
            @RequestParam(name = "goal", required = false) String goal,
            @RequestParam(name = "exercise", required = false) String exerciseName,
            @RequestParam(name = "howMuch", required = false) String howMuchToDo,
            @RequestParam(name = "recommendedTimeToDo", required = false) String recommendedTimeToDo,
            @RequestParam(name = "trial", required = false) String trailsCount,
            @RequestParam(name = "rest", required = false) String timeToRest,
            @RequestParam(name = "someAdvice", required = false) String someAdvice
    ) {
        // System.out.println("trainingName " + trainingName);
        // System.out.println("forWho " + forWho);
        // System.out.println("difficulty " + difficulty);
        // System.out.println("goal " + goal);
        // System.out.println("exercise " + exerciseName);
        // System.out.println("howMuch " + howMuchToDo);
        // System.out.println("recommendedTimeToDo " + recommendedTimeToDo);
        // System.out.println("trial " + trailsCount);
        // System.out.println("rest " + timeToRest);
        // System.out.println("someAdvice " + someAdvice);
        // System.out.println("////////////////////");

        final String[] exercisesNameArr = exerciseName.split(",");
        final String[] howMuchToDoArr = howMuchToDo.split(",");
        final String[] recommendedTimeToDoArr = recommendedTimeToDo.split(",");
        final String[] trailsArr = trailsCount.split(",");
        final String[] timeToRestArr = timeToRest.split(",");

        List<TrainingElement> trainingElements=new ArrayList<>();

        for (int i = 0; i < exercisesNameArr.length; i++) {
            for (int j = 0; j <Integer.parseInt(trailsArr[i]); j++) {
                TrainingElement trainingElement=new TrainingElement();//TODO add in service getOrCreate
                trainingElement.setExercise(exerciseService.findByTitle(exercisesNameArr[i]));
                trainingElement.setHowMuchToDo(Integer.parseInt(howMuchToDoArr[i]));
                trainingElement.setRecommendedTimeToDoLessThan(Integer.parseInt(recommendedTimeToDoArr[i]));
                trainingElement.setTimeToRest(Integer.parseInt(timeToRestArr[i]));

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

        //TODO to add image

        System.out.println(trainingService.save(training));

        return "redirect:/trainings";
    }

}
