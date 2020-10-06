package org.example.controllers;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// import org.example.models.Role;
import org.example.models.User;
import org.example.models.enums.Role;
import org.example.repository.UserRepo;
// import org.example.services.RoleService;
import org.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    UserRepo userRepo;
    @GetMapping
    public String userList(Model model) {
        model.addAttribute("userList", userService.findAll());
        model.addAttribute("allRoles", Role.values());
        return "userList";
    }

    @PostMapping("/edit")
    public String edit(
            @RequestParam Map<String,String> form,
            @RequestParam("userId") User user
    ) {
        System.out.println(form);

        System.out.println("work edit");
        System.out.println(user.getId());
        System.out.println(user.getUsername());
        user.getRoles().forEach(System.out::println);
        System.out.println(" ");

        Set<String> roles= Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());

        user.getRoles().clear();
        for (String key:form.keySet()) {
            if(roles.contains(key)) {
                System.out.println(Role.valueOf(key));
                user.getRoles().add(Role    .valueOf(key));
                System.out.println();
            }
        }
        userRepo.save(user);
        return "redirect:/users";
    }
}
