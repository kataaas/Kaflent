package ru.kataaas.kaflent.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.payload.*;
import ru.kataaas.kaflent.payload.LightGroupDTO;
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
        List<LightGroupDTO> groups = new ArrayList<>();
        List<String> roles = new ArrayList<>();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setAccountNonLocked(user.isAccountNonLocked());
        userDTO.setEnabled(user.isEnabled());

        user.getGroups().forEach(group -> groups.add(groupMapper.toLightGroupDTO(group)));
        user.getRoles().forEach(role -> roles.add(role.getName()));

        userDTO.setGroups(groups);
        userDTO.setRoles(roles);

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
