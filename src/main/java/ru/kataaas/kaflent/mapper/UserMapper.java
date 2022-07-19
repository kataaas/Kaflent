package ru.kataaas.kaflent.mapper;

import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.payload.*;
import ru.kataaas.kaflent.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserMapper {

    public UserDTO toUserDTO(UserEntity user) {
        UserDTO userDTO = new UserDTO();
        List<String> roles = new ArrayList<>();
        user.getRoles().forEach(role -> roles.add(role.getName()));

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setImage(user.getImage());
        userDTO.setGroups(user.getGroups().size());
        userDTO.setRoles(roles);
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setAccountNonLocked(user.isAccountNonLocked());
        userDTO.setEnabled(user.isEnabled());

        return userDTO;
    }

    public UserResponse toUserResponse(List<UserEntity> users, int number, int size, long totalElements, int totalPages, boolean isLast) {
        UserResponse userResponse = new UserResponse();
        List<LightUserDTO> userDTOList = new ArrayList<>();
        users.forEach(user -> userDTOList.add(new LightUserDTO(user.getUsername(), user.getImage())));

        userResponse.setUsers(userDTOList);
        userResponse.setPageNo(number);
        userResponse.setPageSize(size);
        userResponse.setTotalElements(totalElements);
        userResponse.setTotalPages(totalPages);
        userResponse.setLast(isLast);

        return userResponse;
    }

    public AuthUserResponse toAuthResponse(UserEntity user, String accessToken) {
        AuthUserResponse authUserResponse = new AuthUserResponse();
        authUserResponse.setId(user.getId());
        authUserResponse.setUsername(user.getUsername());
        authUserResponse.setAccessToken(accessToken);

        return authUserResponse;
    }

}
