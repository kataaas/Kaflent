package ru.kataaas.kaflent.payload;

import lombok.Data;

import java.util.List;

@Data
public class GroupResponse {

    private List<LightGroupDTO> groups;

    private int pageNo;

    private int pageSize;

    private long totalElements;

    private int totalPages;

    private boolean last;

}
