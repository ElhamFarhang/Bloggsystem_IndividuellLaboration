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
        if (post.isEmpty()) {
            throw new ForbiddenException("There are no posts in the database");
        }
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
        if (post.getContent() == null || post.getContent().length() < 3) {
            throw new ForbiddenException("Post content must be at least 3 characters long");
        }
        post.setOwnerId(sub);
        System.out.println(token.getName() + " with id:" + post.getOwnerId() + " has created a new post");
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
        if (post.getContent() == null || post.getContent().length() < 3) {
            throw new ForbiddenException("Post content must be at least 3 characters long");
        }
        if (!post.getOwnerId().equals(sub)) {
            throw new ForbiddenException("Owner id mismatch");
        }
        postToUpdate.setTitle(post.getTitle());
        postToUpdate.setContent(post.getContent());
        System.out.println("Post with id:" + postToUpdate.getId() + " updated by: " + token.getName() + " with id:" + sub);
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
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("User is not the owner of the post or admin");
        }
        System.out.println("Post with id:" + id + " has been successfully deleted by: " + token.getName() + " with id:" + sub);
        postRepository.deleteById(id);
    }

    public String countAllPosts() {
        List<Post> post = postRepository.findAll();
        return "Number of posts: " + post.size();
    }
}
