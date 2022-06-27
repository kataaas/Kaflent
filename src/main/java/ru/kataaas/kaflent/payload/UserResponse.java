package ru.kataaas.kaflent.payload;

import lombok.Data;

import java.util.List;

@Data
public class UserResponse {

    private List<LightUserDTO> users;

    private int pageNo;

    private int pageSize;

    private long totalElements;

    private int totalPages;

    private boolean last;

}
