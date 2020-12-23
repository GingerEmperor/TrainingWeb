package org.example.controllers;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;

import org.example.exeptions.AlreadyExistsException;
import org.example.models.User;
import org.example.models.enums.Role;
import org.example.models.enums.Status;
import org.example.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @RequestParam String username,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String birthDateStr,
            @RequestParam MultipartFile image,
            Model model) {

        try {
            userService.checkIfExistsByUsername(username);
        }catch (AlreadyExistsException e) {
            model.addAttribute("message", "User with this username already exists");
            return "registration";
        }

        System.out.println(image);
        User userToSave=userService.createUser(username,firstName,lastName,password,email,birthDateStr,image);

        // user.setActive(true);
        // user.setStatus(Status.ACTIVE);
        // user.setBirthDate(Date.valueOf(birthDateStr));
        // user.setRoles(Collections.singleton(Role.USER));
        // user.setRegisteredAt(Date.valueOf(LocalDate.now()));
        userService.save(userToSave);

        return "redirect:/login";
    }
}
