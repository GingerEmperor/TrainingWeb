package org.example.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.models.User;
import org.example.models.enums.Role;
import org.example.models.enums.Status;
import org.example.repository.UserRepo;
import org.example.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("userList", userService.findAll());
        model.addAttribute("allRoles", Role.values());
        List<Status> allStatus= Arrays.asList(Status.values().clone());
        model.addAttribute("allStatus",allStatus);
        return "userList";
    }

    @PatchMapping("/edit")
    public String edit(
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user
    ) {
        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());
        user.getRoles().clear();
        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userService.save(user);
        return "redirect:/admin/users";
    }

    @PatchMapping("{id}/ban")
    public String ban(
            @PathVariable("id") Long userId
    ) {
        User userToBan=userService.findById(userId);
        userToBan.setStatus(Status.BANNED);
        userToBan.setActive(false);
        userService.save(userToBan);
        return "redirect:/admin/users";
    }

    @PatchMapping("{id}/unban")
    public String unBan(
            @PathVariable("id") Long userId
    ) {
        User userToUnBan=userService.findById(userId);
        userToUnBan.setStatus(Status.ACTIVE);
        userToUnBan.setActive(true);
        userService.save(userToUnBan);
        return "redirect:/admin/users";
    }

    @DeleteMapping("{id}")
    public String delete(
            @PathVariable("id") Long userId
    ) {
        User userToDelete=userService.findById(userId);
        System.out.println("Deleting"+userToDelete.getFirstName());
        userService.delete(userToDelete);
        return "redirect:/admin/users";
    }
}
