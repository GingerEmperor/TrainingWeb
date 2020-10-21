package org.example.controllers;

import org.example.exeptions.FileCanNotSaveException;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.services.GlobalService;
import org.example.services.MuscleGroupService;
import org.example.services.MuscleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        model.addAttribute("allMusclesGroup", allMusclesGroup);
        return "muscleGroups";
    }

    @GetMapping("/{id}")
    public String showMusclesByGroupId(@PathVariable Long id,
                                       Model model) {
        MuscleGroup currentMuscleGroup = muscleGroupService.findById(id);
        Set<Muscle> muscles = muscleService.findAllByMuscleGroup(currentMuscleGroup);
        model.addAttribute("allMuscles", muscles);
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
