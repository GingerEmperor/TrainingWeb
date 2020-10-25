package org.example.controllers;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.example.exeptions.FileCanNotSaveException;
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
        Iterable<MuscleGroup> allMusclesGroup = muscleGroupService.findAll();
        model.addAttribute("muscleGroups", allMusclesGroup);
        return "muscleGroups";
    }

    @GetMapping("/{id}")
    public String showMusclesByGroupId(@PathVariable Long id,
                                       Model model) {
        MuscleGroup currentMuscleGroup = muscleGroupService.findById(id);
        List<Muscle> muscles=muscleService.findAllByMuscleGroup(currentMuscleGroup);
        System.out.println("-----before------");
        muscles.forEach(muscle -> System.out.println(muscle.getName()));
        muscles.sort((o1, o2) -> o1.getName().toLowerCase().charAt(0)-o2.getName().toLowerCase().charAt(0));
        System.out.println("---after-----");
        muscles.forEach(muscle -> System.out.println(muscle.getName()));

        //TODO sort all word alphabeticaly
        //TODO Map


        model.addAttribute("muscleGroups", currentMuscleGroup);
        model.addAttribute("currentMuscleGroup",currentMuscleGroup);
        return "muscles";
    }

    @GetMapping("/all")
    public String showAllMuscles() {
        return "redirect:/muscles";
    }

    @PostMapping("/add")
    public String addMuscleGroup(@RequestParam(name = "muscleGroup") String muscleGroupName,
        @RequestParam(name = "file") MultipartFile file
    ) throws IOException {


        if (muscleGroupName.isEmpty())
            return "redirect:/";
        MuscleGroup currentMuscleGroup = muscleGroupService.findByName(muscleGroupName);
        if (currentMuscleGroup != null) {
            throw new RuntimeException("Такая мышечная группа уже есть");
        }
///
        try {
            currentMuscleGroup=muscleGroupService.createNewMuscleGroup(
                    muscleGroupName,
                    globalService.saveImgToPathWithPrefixName(file,uploadPath,muscleGroupName));
        }catch (FileCanNotSaveException e){
            currentMuscleGroup=muscleGroupService.createNewMuscleGroup(muscleGroupName);
        }

        muscleGroupService.save(currentMuscleGroup);
        return "redirect:/muscleGroups";
    }

    @PostMapping("/{id}/delete")
    public String deleteMuscleGroup(@PathVariable(name = "id") Long muscleGroupId) {
        MuscleGroup muscleGroupToDelete = muscleGroupService.findById(muscleGroupId);
        if (muscleGroupToDelete == null) {
            throw new RuntimeException("Невозможно удалить. Такой группы нет");
//            return "redirect:/muscleGroups";
        }
        if(!muscleGroupToDelete.getMuscleSet().isEmpty()) {
//            throw new RuntimeException("Невозможно удалить. Очистите группу");
            Logger logger=Logger.getAnonymousLogger();
            logger.log(Level.WARNING,"Невозможно удалить. Очистите группу");
            return "redirect:/muscleGroups";
        }
        muscleGroupService.deleteMuscleGroup(muscleGroupToDelete);
        return "redirect:/muscleGroups";
    }

}
