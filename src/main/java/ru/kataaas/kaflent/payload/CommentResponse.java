package ru.kataaas.kaflent.payload;

import lombok.Data;

import java.util.List;

@Data
public class CommentResponse {

    private List<CommentDTO> comments;

    private int pageNo;

    private int pageSize;

    private long totalElements;

    private int totalPages;

    private boolean last;

}
