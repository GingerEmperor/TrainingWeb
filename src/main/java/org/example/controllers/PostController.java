package org.example.controllers;

import java.util.List;

import org.example.models.forum.Post;
import org.example.services.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/forum")
public class PostController {

    private final PostService postService;

    @GetMapping()
    public String showForum(Model model) {
        final List<Post> allPosts = postService.findAll();

        model.addAttribute("allPosts", allPosts);
        return "forum";
    }

}
