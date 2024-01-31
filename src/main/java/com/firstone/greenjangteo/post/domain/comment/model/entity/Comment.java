package com.firstone.greenjangteo.post.domain.comment.model.entity;

import com.firstone.greenjangteo.audit.BaseEntity;
import com.firstone.greenjangteo.post.domain.comment.dto.CommentRequestDto;
import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10_000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "comment", cascade = {PERSIST, MERGE, REMOVE}, fetch = FetchType.LAZY)
    private List<Image> images;

    @Builder
    private Comment(Long id, String content, User user, Post post, List<Image> images) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.post = post;
        this.images = images;
    }

    public static Comment of(String content, User user, Post post) {
        return Comment.builder()
                .content(content)
                .user(user)
                .post(post)
                .build();
    }

    public Comment updateFrom(CommentRequestDto commentRequestDto) {
        return Comment.builder()
                .id(id)
                .content(commentRequestDto.getContent())
                .user(user)
                .post(post)
                .images(images)
                .build();
    }
}
