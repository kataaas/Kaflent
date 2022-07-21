package ru.kataaas.kaflent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kataaas.kaflent.entity.PostEmotion;
import ru.kataaas.kaflent.entity.PostEntity;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.repository.EmotionRepository;
import ru.kataaas.kaflent.utils.EmotionsTypeEnum;

@Service
public class EmotionService {

    private final UserService userService;

    private final PostService postService;

    private final EmotionRepository emotionRepository;

    @Autowired
    public EmotionService(UserService userService,
                          @Lazy PostService postService,
                          EmotionRepository emotionRepository) {
        this.userService = userService;
        this.postService = postService;
        this.emotionRepository = emotionRepository;
    }

    public void createEmotion(Long userId, Long postId, String emotion) {
        PostEmotion postEmotion = emotionRepository.findByUserIdAndPostId(userId, postId);
        if (postEmotion == null) {
            postEmotion = new PostEmotion();
            UserEntity user = userService.findById(userId);
            PostEntity post = postService.findById(postId);

            postEmotion.setPostId(postId);
            postEmotion.setUserId(userId);
            postEmotion.setPostMapping(post);
            postEmotion.setUserMapping(user);
        }
        postEmotion.setEmotion(EmotionsTypeEnum.valueOf(emotion.toUpperCase()));

        emotionRepository.save(postEmotion);
    }

    @Transactional
    public void deleteEmotion(Long userId, Long postId) {
        emotionRepository.deleteByUserIdAndPostId(userId, postId);
    }

    public Page<String> getEmotionsByPostId(Long postId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return emotionRepository.getEmotionsByPostId(postId, pageable);
    }

    public int getCountOfEmotions(Long postId) {
        return emotionRepository.countByPostId(postId);
    }

}
