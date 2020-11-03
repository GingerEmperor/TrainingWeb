package org.example.controllers;

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

@Controller
public class RegistrationController {
    @Autowired
    private  UserRepo userRepo;
    // @Autowired
    // private RoleService roleService;

    // @Autowired
    // private RoleRepository roleRepo;

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model){//TODO can not принять user because birthDate

        User userFromDb= userRepo.findByUsername(user.getUsername());
        if(userFromDb != null){
            model.addAttribute("message","User with this username already exists");
            return "registration";
        }
        System.out.println("regWork");
        user.setActive(true);
        user.setStatus(Status.ACTIVE);
        user.setRoles(Collections.singleton(Role.USER));
        // user.setRoles(Collections.singleton(roleRepo.findByRoleType(Role.USER)));
        userRepo.save(user);

        return "redirect:/login";
    }
}
