package ru.kataaas.kaflent.mapper;

import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.payload.RoleDTO;
import ru.kataaas.kaflent.entity.RoleEntity;

@Service
public class RoleMapper {

    public RoleDTO toRoleDTO(RoleEntity role) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());

        return roleDTO;
    }

}
