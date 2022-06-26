package ru.kataaas.kaflent.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.kataaas.kaflent.entity.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Page<CommentEntity> findAllByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

    int countAllByPostId(Long postId);

}
