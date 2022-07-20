package ru.kataaas.kaflent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kataaas.kaflent.entity.PostEmotion;
import ru.kataaas.kaflent.entity.PostEmotionKey;

public interface EmotionRepository extends JpaRepository<PostEmotion, PostEmotionKey> {

    PostEmotion findByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

    int countByPostId(Long postId);

}
