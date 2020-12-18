package org.example.controllers;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;

import org.example.models.User;
import org.example.models.enums.Role;
// import org.example.repository.RoleRepository;
import org.example.models.enums.Status;
import org.example.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {
    @Autowired
    private  UserRepo userRepo;

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, @RequestParam String birthDateStr, Model model){
        System.out.println(birthDateStr);
        System.out.println(Date.valueOf(birthDateStr));

        User userFromDb= userRepo.findByUsername(user.getUsername());
        if(userFromDb != null){
            model.addAttribute("message","User with this username already exists");
            return "registration";
        }
        System.out.println("regWork");
        user.setActive(true);
        user.setStatus(Status.ACTIVE);
        user.setBirthDate(Date.valueOf(birthDateStr));
        user.setRoles(Collections.singleton(Role.USER));
        user.setRegisteredAt(Date.valueOf(LocalDate.now()));
        userRepo.save(user);

        return "redirect:/login";
    }
}
