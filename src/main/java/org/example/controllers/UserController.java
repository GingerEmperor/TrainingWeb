package org.example.controllers;

import java.util.List;

import org.example.models.User;
import org.example.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
@PreAuthorize("hasAuthority('USER')")
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public String getUsers(Model model){
        List<User> allUsers = userService.findAll();
        model.addAttribute("users",allUsers);
        return "userPages/users";
    }

    @GetMapping("/{username}")
    public String getUserInfoByUsername(@PathVariable String username,Model model){
        User user = userService.findByUsername(username);
        model.addAttribute("user",user);
        return "userPages/profile";
    }
}


