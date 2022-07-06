package ru.kataaas.kaflent.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LightUserDTO {

    private String username;

    private String image;

}
