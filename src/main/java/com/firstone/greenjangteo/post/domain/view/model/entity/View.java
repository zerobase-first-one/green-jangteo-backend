package com.firstone.greenjangteo.post.domain.view.model.entity;

import com.firstone.greenjangteo.audit.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "view")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "view")
public class View extends BaseEntity {
    @Id
    private Long postId;

    private int viewCount;

    public View(Long postId) {
        this.postId = postId;
    }

    public void addViewCount() {
        ++viewCount;
    }
}
