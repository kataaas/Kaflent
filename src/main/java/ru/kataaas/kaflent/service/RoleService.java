package ru.kataaas.kaflent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.entity.RoleEntity;
import ru.kataaas.kaflent.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleEntity findByName(String name) {
        return roleRepository.findByName(name);
    }
}
