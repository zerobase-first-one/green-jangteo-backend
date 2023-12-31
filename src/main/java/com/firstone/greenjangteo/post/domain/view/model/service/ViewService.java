package com.firstone.greenjangteo.post.domain.view.model.service;

import com.firstone.greenjangteo.post.domain.view.model.entity.View;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewService {
    private final ViewRepository viewRepository;

    public View addAndGetView(Long postId) {
        View view = viewRepository.findById(postId).orElse(new View(postId));

        view.addViewCount();
        return viewRepository.save(view);
    }

    public View getView(Long postId) {
        return viewRepository.findById(postId).orElse(new View(postId));
    }
}
