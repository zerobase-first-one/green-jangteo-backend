package com.firstone.greenjangteo.post.domain.image.model.entity;

import com.firstone.greenjangteo.audit.BaseEntity;
import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import com.firstone.greenjangteo.post.model.entity.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "image")
public class Image extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private String url;

    @Column(name = "position", unique = true)
    private int positionInContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    private Image(String url, int positionInContent, Post post) {
        this.url = url;
        this.positionInContent = positionInContent;
        this.post = post;
    }

    public static Image from(Post post, ImageRequestDto imageRequestDto) {
        return Image.builder()
                .url(imageRequestDto.getUrl())
                .positionInContent(imageRequestDto.getPositionInContent())
                .post(post)
                .build();
    }
}
