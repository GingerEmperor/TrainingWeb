package org.example.controllers;

import java.util.List;

import org.example.exeptions.NotFoundException;
import org.example.models.User;
import org.example.models.dtos.UserDto;
import org.example.models.forum.Post;
import org.example.services.GlobalService;
import org.example.services.PostService;
import org.example.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final PostService postService;

    private final GlobalService globalService;

    @GetMapping("")
    public String getUsers(Model model) {
        List<User> allUsers = userService.findAll();

        model.addAttribute("users", allUsers);
        return "userPages/users";
    }

    @GetMapping("/{username}")
    public String getUserInfoByUsername(@PathVariable String username, Model model) {
        User user = userService.findByUsername(username);
        final List<Post> allUserPosts = postService.findAllByAuthorId(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("allUserPosts", allUserPosts);
        return "userPages/profile";
    }

    @GetMapping("/edit/{username}")
    public String getEditUserInfoPage(@PathVariable String username, Model model) {

        globalService.checkAccessByUsername(username);
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "userPages/editUser";
    }

    @PutMapping("/edit/{username}")
    public String editUserInfo(UserDto newUserDto, Model model) {
        User updatedUser;
        try {
            User userToUpdate = userService.findByUsername(newUserDto.getUsername());
            updatedUser = userService.updateUser(userToUpdate, newUserDto);
        } catch (NotFoundException e) {
            updatedUser = userService.createUser(newUserDto);
        }
        userService.save(updatedUser);

        return "redirect:/login";
    }
}


