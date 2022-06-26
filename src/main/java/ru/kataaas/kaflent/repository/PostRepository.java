package ru.kataaas.kaflent.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kataaas.kaflent.entity.PostEntity;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Page<PostEntity> findAllByGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);

    Page<PostEntity> findAllByGroupIdIsInOrderByCreatedAtDesc(List<Long> ids, Pageable pageable);
}
