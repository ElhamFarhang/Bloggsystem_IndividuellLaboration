package com.example.bloggsystem_individuelllaboration.cotrollers;

import com.example.bloggsystem_individuelllaboration.entities.Post;
import com.example.bloggsystem_individuelllaboration.services.PostServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v2")
public class PostController {

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

    @PostMapping("/newpost")
    public ResponseEntity<Post> addPost(@RequestBody Post post, Authentication authentication) {
        return ResponseEntity.ok(postServiceImpl.addNewPost(post, authentication));
    }

    @PutMapping("/updatepost")
    public ResponseEntity<Post> updatePost(@RequestBody Post post, Authentication authentication) {
        return ResponseEntity.ok(postServiceImpl.updatePost(post, authentication));
    }

    @DeleteMapping("/deletepost/{id}")
    public ResponseEntity<String> deletePostById(@PathVariable("id") Long id, Authentication authentication) {
        postServiceImpl.deletePostById(id, authentication);
        return ResponseEntity.ok("Post with Id: " + id + " has been successfully deleted.");
    }

    @GetMapping("/count")
    public ResponseEntity<String> countPosts() {
        return ResponseEntity.ok(postServiceImpl.countAllPosts());
    }
}
