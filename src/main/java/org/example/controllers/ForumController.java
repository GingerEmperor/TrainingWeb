package org.example.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.example.models.User;
import org.example.models.forum.Post;
import org.example.services.PostService;
import org.example.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/forum")
public class ForumController {

    private final PostService postService;

    private final UserService userService;

    @GetMapping("/{requestedUserName}")
    public String showSubscriptionsForum(@PathVariable String requestedUserName, Model model) {
        final List<Post> subscriptionPosts = new ArrayList<>();
        System.out.println("");

        User requestedUser = userService.findByUsername(requestedUserName);
        requestedUser.getSubscriptions().forEach(subscription -> subscriptionPosts.addAll(postService.findAllByAuthorId(subscription.getId())));

        model.addAttribute("allPosts", subscriptionPosts);
        return "forum";
    }

    @GetMapping("/all")
    public String showAllForum(Model model) {
        final List<Post> allPosts = postService.findAll();
        final List<Post> sortedPosts = allPosts.stream().sorted(Comparator.comparing(Post::getPostTime).reversed()).collect(Collectors.toList());

        model.addAttribute("allPosts", sortedPosts);
        return "forum";
    }

}
