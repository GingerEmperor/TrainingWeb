package org.example.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import static java.lang.String.format;

import org.example.exeptions.NotFoundException;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.services.GlobalService;
import org.example.services.MuscleGroupService;
import org.example.services.MuscleService;
import org.example.utill.alerts.Alert;
import org.example.utill.alerts.DangerAlert;
import org.example.utill.alerts.InfoAlert;
import org.example.utill.alerts.SuccessAlert;
import org.example.utill.alerts.WarningAlert;
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
import static org.example.utill.StringsFormatsUtils.MUSCLE_GROUP_WAS_ADDED_FORMAT;
import static org.example.utill.StringsFormatsUtils.MUSCLE_GROUP_WAS_DELETED_FORMAT;
import static org.example.utill.StringsFormatsUtils.MUSCLE_GROUP_WAS_UPDATED_FORMAT;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/muscleGroups")
public class MuscleGroupController {

    private final MuscleGroupService muscleGroupService;

    private final MuscleService muscleService;

    private final GlobalService globalService;

    @GetMapping()
    public String showAllMusclesGroup(Model model) {
        List<MuscleGroup> allMusclesGroup = muscleGroupService.findAll();
        allMusclesGroup.sort((o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));
        model.addAttribute("muscleGroups", allMusclesGroup);
        model.addAttribute("alert", alert.poll());
        return "muscleTemplates/muscleGroups";
    }

    @GetMapping("/{id}")
    public String showMusclesByGroupId(@PathVariable Long id,
            Model model) {
        Map<MuscleGroup, List<Muscle>> muscleGroupMusclesMap = new TreeMap<>();
        MuscleGroup currentMuscleGroup;
        try {
            currentMuscleGroup = muscleGroupService.findById(id);
        } catch (NotFoundException e) {
            e.printStackTrace();
            alert.add(new DangerAlert(e.getMessage()));
            return "redirect:/muscleGroups";
        }
        List<Muscle> muscles = muscleService.findAllByMuscleGroup(currentMuscleGroup);
        muscles.sort((o1, o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));
        muscleGroupMusclesMap.put(currentMuscleGroup, muscles);

        model.addAttribute("muscleGroupMusclesMap", muscleGroupMusclesMap);
        model.addAttribute("currentMuscleGroup", currentMuscleGroup);
        model.addAttribute("alert", alert.poll());

        return "muscleTemplates/muscles";
    }

    @GetMapping("/all")
    public String showAllMuscles() {
        return "redirect:/muscles";
    }

    @PostMapping("/add")
    public String addMuscleGroup(@RequestParam(name = "muscleGroup") String muscleGroupName,
            @RequestParam(name = "file") MultipartFile file
    ) {
        try {
            globalService.checkIfNameIsValid(muscleGroupName);
            muscleGroupService.createNewMuscleGroup(muscleGroupName, file);
            alert.add(new SuccessAlert(format(MUSCLE_GROUP_WAS_ADDED_FORMAT, muscleGroupName)));
        } catch (Exception e) {
            e.printStackTrace();
            alert.add(new DangerAlert(e.getMessage()));
        }
        return "redirect:/muscleGroups";
    }

    @PatchMapping("/{id}")
    public String editMuscleGroup(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "newName") String newName,
            @RequestParam(name = "imageFile") MultipartFile imageFile
    ) {
        try {
            globalService.checkIfNameIsValid(newName);
            final MuscleGroup muscleGroupToUpdate = muscleGroupService.findById(id);
            if(muscleGroupToUpdate!=null && !muscleGroupToUpdate.getName().equals(newName)){
                muscleGroupService.checkIfExistsMuscleGroupByName(newName);
            }
            MuscleGroup updatedMuscleGroup;

            if (imageFile != null && !imageFile.getOriginalFilename().isEmpty()) {
                updatedMuscleGroup = muscleGroupService.updateMuscleGroup(id, newName.trim(), imageFile);
            } else {
                updatedMuscleGroup = muscleGroupService.updateMuscleGroup(id, newName.trim());
            }
            muscleGroupService.save(updatedMuscleGroup);
            alert.add(new InfoAlert(format(MUSCLE_GROUP_WAS_UPDATED_FORMAT, newName)));
        } catch (Exception e) {
            e.printStackTrace();
            alert.add(new DangerAlert(e.getMessage()));
        }
        return "redirect:/muscleGroups";
    }

    @DeleteMapping("/{id}/delete")
    public String deleteMuscleGroup(@PathVariable(name = "id") Long muscleGroupId, Model model) {
        try {
            MuscleGroup muscleGroupToDelete = muscleGroupService.findById(muscleGroupId);
            muscleGroupService.deleteMuscleGroup(muscleGroupToDelete);
            alert.add(new WarningAlert(format(MUSCLE_GROUP_WAS_DELETED_FORMAT, muscleGroupToDelete.getName())));
        } catch (Exception e) {
            System.out.println("OSIBKA");//TODO add some handler
            e.printStackTrace();
            alert.add(new DangerAlert(e.getMessage()));
        }
        return "redirect:/muscleGroups";
    }

}
