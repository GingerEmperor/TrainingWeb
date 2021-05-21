package org.example.controllers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
public class ForumController {

    private final PostService postService;

    @GetMapping()
    public String showForum(Model model) {
        final List<Post> allPosts = postService.findAll();
        final List<Post> sortedPosts = allPosts.stream().sorted(Comparator.comparing(Post::getPostTime).reversed()).collect(Collectors.toList());

        model.addAttribute("allPosts", sortedPosts);
        return "forum";
    }

}
