package ru.kataaas.kaflent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.entity.CommentEntity;
import ru.kataaas.kaflent.mapper.CommentMapper;
import ru.kataaas.kaflent.payload.CommentResponse;
import ru.kataaas.kaflent.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Autowired
    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public CommentEntity save(CommentEntity comment) {
        return commentRepository.save(comment);
    }

    public void delete(CommentEntity comment) {
        commentRepository.delete(comment);
    }

    public CommentEntity findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public CommentResponse getAllCommentsByPostId(Long postId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<CommentEntity> comments = commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId, pageable);
        return commentMapper.toCommentResponse(comments);
    }

    public int countOfComments(Long postId) {
        return commentRepository.countAllByPostId(postId);
    }

}
