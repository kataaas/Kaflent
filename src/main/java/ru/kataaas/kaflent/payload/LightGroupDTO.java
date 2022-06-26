package ru.kataaas.kaflent.payload;

import lombok.Data;

@Data
public class LightGroupDTO {

    private String name;

    private String type;

    private int subscribers;

}
