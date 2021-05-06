package org.example.controllers;

import org.example.models.forum.Post;
import org.example.services.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @DeleteMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        final Post postToDelete = postService.findById(id);
        postService.deletePost(postToDelete);
        return "redirect:/";
    }
}
