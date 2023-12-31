package com.firstone.greenjangteo.post.domain.view.model.service;

import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.post.repository.PostRepository;
import com.firstone.greenjangteo.post.utility.PostTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.firstone.greenjangteo.post.utility.PostTestConstant.CONTENT1;
import static com.firstone.greenjangteo.post.utility.PostTestConstant.SUBJECT1;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class ViewServiceTest {
    @Autowired
    private ViewService viewService;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("게시물 ID를 전송해 조회수를 추가하고 조회수 객체를 생성하거나 조회할 수 있다.")
    @Test
    void addAndGetView() {
        // given
        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        // when
        int viewCount1 = viewService.addAndGetView(post.getId()).getViewCount();
        int viewCount2 = viewService.addAndGetView(post.getId()).getViewCount();
        int viewCount3 = viewService.addAndGetView(post.getId()).getViewCount();

        // then
        assertThat(viewCount1).isEqualTo(1);
        assertThat(viewCount2).isEqualTo(2);
        assertThat(viewCount3).isEqualTo(3);
    }

    @DisplayName("게시물 ID를 전송해 조회수 객체를 생성하거나 조회할 수 있다.")
    @Test
    void getView() {
        // given
        Post post = PostTestObjectFactory.createPost(SUBJECT1, CONTENT1);
        postRepository.save(post);

        // when
        int viewCount1 = viewService.getView(post.getId()).getViewCount();
        viewService.addAndGetView(post.getId());
        int viewCount2 = viewService.getView(post.getId()).getViewCount();

        // then
        assertThat(viewCount1).isEqualTo(0);
        assertThat(viewCount2).isEqualTo(1);
    }
}
