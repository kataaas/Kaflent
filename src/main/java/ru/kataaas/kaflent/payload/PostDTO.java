package ru.kataaas.kaflent.payload;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class PostDTO {

    private Long id;

    private String content;

    private Set<String> files;

    private Date createdAt;

    private Long groupId;

    private int countOfComments;

}
