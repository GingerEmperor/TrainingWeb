package org.example.controllers;

import org.example.exeptions.AlreadyExistsException;
import org.example.models.User;
import org.example.models.dtos.UserDto;
import org.example.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String addUserTemplate(
            UserDto userDto,
            Model model) {

        try {
            userService.checkIfExistsByUsername(userDto.getUsername());
        } catch (AlreadyExistsException e) {
            model.addAttribute("message", "User with this username already exists");
            return "registration";
        }

        User userToSave = userService.createUserTemplate(userDto);

        model.addAttribute("user", userToSave);
        return "/userPages/editUser";
    }
}
