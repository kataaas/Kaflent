package ru.kataaas.kaflent.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kataaas.kaflent.entity.PostEmotion;
import ru.kataaas.kaflent.entity.PostEmotionKey;

public interface EmotionRepository extends JpaRepository<PostEmotion, PostEmotionKey> {

    PostEmotion findByUserIdAndPostId(Long userId, Long postId);

    @Query(value = "SELECT e.emotion FROM post_emotions e WHERE e.post_id = :postId", nativeQuery = true)
    Page<String> getEmotionsByPostId(@Param("postId") Long postId, Pageable pageable);

    void deleteByUserIdAndPostId(Long userId, Long postId);

    int countByPostId(Long postId);

}
