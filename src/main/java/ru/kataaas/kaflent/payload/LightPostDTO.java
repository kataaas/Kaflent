package ru.kataaas.kaflent.payload;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class LightPostDTO {

    private String content;

    @Size(max = 9)
    private Set<String> files;

    private Long groupId;

}
