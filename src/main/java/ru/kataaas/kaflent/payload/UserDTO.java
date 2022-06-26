package ru.kataaas.kaflent.payload;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDTO {

    private Long id;

    private String username;

    private List<LightGroupDTO> groups;

    private List<String> roles;

    private Date createdAt;

    private boolean accountNonLocked;

    private boolean enabled;

}
