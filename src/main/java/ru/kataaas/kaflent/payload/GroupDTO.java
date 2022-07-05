package ru.kataaas.kaflent.payload;

import lombok.Data;

import java.util.Date;

@Data
public class GroupDTO {

    private Long id;

    private String name;

    private String image;

    private String type;

    private int subscribers;

    private PostResponse posts;

    private Date createdAt;

}
