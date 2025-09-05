package com.example.bloggsystem_individuelllaboration.cotrollers;

import com.example.bloggsystem_individuelllaboration.entities.Post;
import com.example.bloggsystem_individuelllaboration.exceptions.ForbiddenException;
import com.example.bloggsystem_individuelllaboration.repositories.PostRepository;
import com.example.bloggsystem_individuelllaboration.services.PostServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v2")
public class PostController {

    @GetMapping("/test")
    public String testEndpoint(org.springframework.security.core.Authentication authentication) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String sub = token.getToken().getClaim("sub");
        return "User ID (sub): " + sub;
    }

    private PostServiceImpl postServiceImpl;

    public PostController(PostServiceImpl postServiceImpl) {
        this.postServiceImpl = postServiceImpl;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postServiceImpl.getAllPosts());
    }

    @GetMapping("/post/{id}")
    public ResponseEntity <Optional<Post>> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postServiceImpl.getPostById(id));
    }

    @PreAuthorize("hasRole('bloggUser')")
    @PostMapping("/newpost")
    public ResponseEntity<Post> addPost(@RequestBody Post post, Authentication authentication) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String sub = token.getToken().getClaim("sub");
        post.setOwnerId(sub);
        return ResponseEntity.ok(postServiceImpl.addNewPost(post, authentication));
    }

    @PreAuthorize("hasRole('bloggUser')")
    @PutMapping("/updatepost")
    public ResponseEntity<Post> updatePost(@RequestBody Post post, Authentication authentication) {
        if (post.getContent() == null || post.getContent().length() < 3) {
            throw new ForbiddenException("Post content must be at least 3 characters long");
        }
        return ResponseEntity.ok(postServiceImpl.updatePost(post, authentication));
    }

    @PreAuthorize("hasRole('bloggAdmin') or hasRole('bloggUser')")
    @DeleteMapping("/deletepost/{id}")
    public ResponseEntity<String> deletePostById(@PathVariable("id") Long id, Authentication authentication) {
        postServiceImpl.deletePostById(id, authentication);
        return ResponseEntity.ok("Post with Id: " + id + " has been successfully deleted.");
    }

    @PreAuthorize("hasRole('bloggAdmin')")
    @GetMapping("/count")
    public ResponseEntity<String> countPosts() {
        return ResponseEntity.ok(postServiceImpl.countAllPosts());
    }
}
