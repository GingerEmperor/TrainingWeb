package org.example.repository;

import java.util.List;

import org.example.models.forum.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findAll();

}
