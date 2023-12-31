package com.firstone.greenjangteo.post.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.firstone.greenjangteo.audit.BaseEntity;
import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import com.firstone.greenjangteo.post.dto.PostRequestDto;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int view;

    @OneToMany(mappedBy = "post", cascade = {PERSIST, MERGE, REMOVE}, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Image> images;

    @Builder
    private Post(Long id, String subject, String content, User user, List<Image> images) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.user = user;
        this.images = images;
    }

    public static Post from(PostRequestDto postRequestDto, User user) {
        return Post.builder()
                .subject(postRequestDto.getSubject())
                .content(postRequestDto.getContent())
                .user(user)
                .build();
    }
}
