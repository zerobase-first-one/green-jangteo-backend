package com.firstone.greenjangteo.post.domain.image.service;

import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import com.firstone.greenjangteo.post.domain.image.repository.ImageRepository;
import com.firstone.greenjangteo.post.model.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public void saveImages(Post post, List<ImageRequestDto> imageRequestDtos) {
        List<Image> images = imageRequestDtos.stream()
                .map(imageRequestDto -> Image.from(post, imageRequestDto))
                .collect(Collectors.toList());

        imageRepository.saveAll(images);
    }

    /**
     * 클라이언트가 삭제한 이미지는 image 테이블에서 제거, 추가한 이미지는 image 테이블에 추가
     *
     * @param post             이미지 데이터 목록이 저장된 게시글
     * @param imageRequestDtos 새로 갱신될 이미지 데이터 목록
     */
    public void updateImages(Post post, List<ImageRequestDto> imageRequestDtos) {
        Long postId = post.getId();

        if (imageRequestDtos == null || imageRequestDtos.isEmpty()) {
            deleteAllPostImages(postId);
            return;
        }

        List<Image> images = imageRepository.findAllByPostIdOrderByIdAsc(postId);

        List<Image> imagesToDelete = new ArrayList<>();
        List<Image> imagesToSave = new ArrayList<>();

        int startingIdx = 0;

        for (ImageRequestDto imageRequestDto : imageRequestDtos) {
            boolean isSameImage = false;

            for (int i = startingIdx; i < images.size(); i++) {
                if (checkSameUrlAndPosition(images, imageRequestDto, i)) {
                    isSameImage = true;
                    startingIdx = i + 1;
                    break;
                } else {
                    imagesToDelete.add(images.get(i));
                }
            }

            if (!isSameImage) {
                imagesToSave.add(Image.from(post, imageRequestDto));
            }
        }

        imageRepository.deleteAllInList(imagesToDelete);
        imageRepository.saveAll(imagesToSave);
    }

    public void saveImages(Comment comment, List<ImageRequestDto> imageRequestDtos) {
        List<Image> images = imageRequestDtos.stream()
                .map(imageRequestDto -> Image.from(comment, imageRequestDto))
                .collect(Collectors.toList());

        imageRepository.saveAll(images);
    }

    public void updateImages(Comment comment, List<ImageRequestDto> imageRequestDtos) {
        Long commentId = comment.getId();

        if (imageRequestDtos == null || imageRequestDtos.isEmpty()) {
            deleteAllCommentImages(commentId);
            return;
        }

        List<Image> images = imageRepository.findAllByCommentIdOrderByIdAsc(commentId);

        List<Image> imagesToDelete = new ArrayList<>();
        List<Image> imagesToSave = new ArrayList<>();

        int startingIdx = 0;

        for (ImageRequestDto imageRequestDto : imageRequestDtos) {
            boolean isSameImage = false;

            for (int i = startingIdx; i < images.size(); i++) {
                if (checkSameUrlAndPosition(images, imageRequestDto, i)) {
                    isSameImage = true;
                    startingIdx = i + 1;
                    break;
                } else {
                    imagesToDelete.add(images.get(i));
                }
            }

            if (!isSameImage) {
                imagesToSave.add(Image.from(comment, imageRequestDto));
            }
        }

        imageRepository.deleteAllInList(imagesToDelete);
        imageRepository.saveAll(imagesToSave);
    }

    private void deleteAllPostImages(Long postId) {
        imageRepository.deleteByPostId(postId);
    }

    private void deleteAllCommentImages(Long commentId) {
        imageRepository.deleteByCommentId(commentId);
    }

    private boolean checkSameUrlAndPosition(List<Image> images, ImageRequestDto imageRequestDto, int idx) {
        if (!images.get(idx).getUrl().equals(imageRequestDto.getUrl())) {
            return false;
        }

        if (images.get(idx).getPositionInContent() != imageRequestDto.getPositionInContent()) {
            return false;
        }

        return true;
    }
}
