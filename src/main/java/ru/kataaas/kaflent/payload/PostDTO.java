package ru.kataaas.kaflent.payload;

import lombok.Data;

import java.util.Date;

@Data
public class PostDTO {

    private Long id;

    private String content;

    private Date createdAt;

    private Long groupId;

    private int countOfComments;

}
