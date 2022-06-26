package ru.kataaas.kaflent.payload;

import lombok.Data;

import java.util.Date;

@Data
public class CommentDTO {

    private Long id;

    private String content;

    private Long userId;

    private Date createdAt;

}
