package ru.kataaas.kaflent.payload;

import lombok.Data;

import java.util.List;

@Data
public class PostResponse {

    private List<PostDTO> posts;

    private int pageNo;

    private int pageSize;

    private long totalElements;

    private int totalPages;

    private boolean last;

}
