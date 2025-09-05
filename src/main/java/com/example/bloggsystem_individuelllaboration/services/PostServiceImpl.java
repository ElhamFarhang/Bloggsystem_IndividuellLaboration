package com.example.bloggsystem_individuelllaboration.services;

import com.example.bloggsystem_individuelllaboration.entities.Post;
import com.example.bloggsystem_individuelllaboration.exceptions.ForbiddenException;
import com.example.bloggsystem_individuelllaboration.exceptions.ResourceNotFoundException;
import com.example.bloggsystem_individuelllaboration.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl{

    private PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        List<Post> post = postRepository.findAll();
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (!post.isPresent()) {
            throw new ResourceNotFoundException("Post", "id", id);
        }
        return post;
    }

    public Post addNewPost(Post post, Authentication authentication) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String sub = token.getToken().getClaim("sub");
        if (!post.getOwnerId().equals(sub)) {
            throw new RuntimeException("Post owner is not the owner of the post");
        }
        System.out.println("Creating post by Keycloak sub: " + token.getName());
        return  postRepository.save(post);
    }

    public Post updatePost(Post post, Authentication authentication) {
        Optional<Post> postOptional = postRepository.findById(post.getId());
        if (!postOptional.isPresent()) {
            throw new ResourceNotFoundException("Post", "id", post.getId());
        }
        Post postToUpdate = postOptional.get();
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String sub = token.getToken().getClaim("sub");
        if (!postToUpdate.getOwnerId().equals(sub)) {
            throw new ForbiddenException("User is not the owner of the post");
        }

        postToUpdate.setTitle(post.getTitle());
        postToUpdate.setContent(post.getContent());
        return postRepository.save(postToUpdate);
    }

    public void deletePostById(Long id, Authentication authentication)  {
        Optional<Post> post = postRepository.findById(id);
        if (!post.isPresent()) {
            throw new ResourceNotFoundException("Post", "id", id);
        }
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String sub = token.getToken().getClaim("sub");
        boolean isOwner = post.get().getOwnerId().equals(sub);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_bloggAdmin"));

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("User is not the owner of the post or admin");
        }
        postRepository.deleteById(id);
    }

    public String countAllPosts() {
        List<Post> post = postRepository.findAll();
        return "Number of posts: " + post.size();
    }
}
