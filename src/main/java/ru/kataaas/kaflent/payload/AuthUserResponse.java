package ru.kataaas.kaflent.payload;

import lombok.Data;

@Data
public class AuthUserResponse {

    private Long id;

    private String username;

    private String accessToken;

}
