package com.example.bloggsystem_individuelllaboration.repositories;

import com.example.bloggsystem_individuelllaboration.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByOwnerId(String ownerId);
}
