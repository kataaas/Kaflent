package ru.kataaas.kaflent.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.payload.*;
import ru.kataaas.kaflent.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserMapper {

    private final RoleMapper roleMapper;

    private final GroupMapper groupMapper;

    @Autowired
    public UserMapper(RoleMapper roleMapper, GroupMapper groupMapper) {
        this.roleMapper = roleMapper;
        this.groupMapper = groupMapper;
    }

    public UserDTO toUserDTO(UserEntity user) {
        UserDTO userDTO = new UserDTO();
        List<String> roles = new ArrayList<>();
        user.getRoles().forEach(role -> roles.add(role.getName()));

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setGroups(user.getGroups().size());
        userDTO.setRoles(roles);
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setAccountNonLocked(user.isAccountNonLocked());
        userDTO.setEnabled(user.isEnabled());

        return userDTO;
    }

    public UserResponse toUserResponse(Page<UserEntity> users) {
        UserResponse userResponse = new UserResponse();
        List<LightUserDTO> userDTOList = new ArrayList<>();
        List<UserEntity> userEntities = users.getContent();
        userEntities.forEach(user -> userDTOList.add(new LightUserDTO(user.getUsername())));

        userResponse.setUsers(userDTOList);
        userResponse.setPageNo(users.getNumber());
        userResponse.setPageSize(users.getSize());
        userResponse.setTotalElements(users.getTotalElements());
        userResponse.setTotalPages(users.getTotalPages());
        userResponse.setLast(users.isLast());

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
