package ru.kataaas.kaflent.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.payload.CommentDTO;
import ru.kataaas.kaflent.payload.CommentResponse;
import ru.kataaas.kaflent.entity.CommentEntity;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentMapper {

    public CommentResponse toCommentResponse(Page<CommentEntity> comments) {
        CommentResponse commentResponse = new CommentResponse();
        List<CommentDTO> commentDTOS = new ArrayList<>();
        // get post content from page and convert to postDTO
        comments.getContent().stream().forEach(comment -> commentDTOS.add(toCommentDTO(comment)));

        commentResponse.setComments(commentDTOS);
        commentResponse.setPageNo(comments.getNumber());
        commentResponse.setPageSize(comments.getSize());
        commentResponse.setTotalElements(comments.getTotalElements());
        commentResponse.setLast(comments.isLast());

        return commentResponse;
    }

    public CommentDTO toCommentDTO(CommentEntity comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setUserId(comment.getUserId());
        commentDTO.setCreatedAt(comment.getCreatedAt());

        return commentDTO;
    }

}
