package org.example.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.exeptions.NotFoundException;
import org.example.models.Training;
import org.example.models.User;
import org.example.models.forum.Post;
import org.example.repository.PostRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public List<Post>findAllByAuthorId(Long id){
        return postRepository.findAllByAuthorId(id);
    }

    public void createPost(User author, String shortText, String fullText, Training training) {
        Post post = Post.builder()
                .author(author)
                .postTime(LocalDateTime.now())
                .shortText(shortText)
                .text(fullText)
                .training(training)
                .build();
        //TODO add image
        post.setTrainingImage(training.getImage());
        post.setUserImage(author.getImage());

        postRepository.save(post);
    }

    public void deletePost(Post post){
        postRepository.delete(post);
    }

    public Post findById(final Long id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException("Такого поста ["+id+"] нет в базе данных"));
    }
}
