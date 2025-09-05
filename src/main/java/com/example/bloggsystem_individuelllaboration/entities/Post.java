package com.example.bloggsystem_individuelllaboration.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(length = 5000,  nullable = false)
    private String content;

    @Column(length = 100, nullable = false)
    private String ownerId;

    public Post() {
    }

    public Post(String content, Long id, String ownerId, String title) {
        this.content = content;
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Post{" +
                "content='" + content + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", ownerId=" + ownerId +
                '}';
    }
}
