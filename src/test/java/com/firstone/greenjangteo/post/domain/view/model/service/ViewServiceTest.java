package com.firstone.greenjangteo.post.domain.view.model.service;

import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.post.repository.PostRepository;
import com.firstone.greenjangteo.post.utility.PostTestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.firstone.greenjangteo.post.utility.PostTestConstant.CONTENT;
import static com.firstone.greenjangteo.post.utility.PostTestConstant.SUBJECT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class ViewServiceTest {
    @Autowired
    private ViewService viewService;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("조회수를 추가하고 조회할 수 있다.")
    @Test
    void addAndGetView() {
        // given
        Post post = PostTestObjectFactory.createPost(SUBJECT, CONTENT);
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
}
