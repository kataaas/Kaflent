package ru.kataaas.kaflent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kataaas.kaflent.entity.GroupEntity;
import ru.kataaas.kaflent.entity.GroupUser;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.repository.GroupRepository;
import ru.kataaas.kaflent.utils.GroupTypeEnum;

@Service
public class GroupService {

    private final UserService userService;

    private final GroupRepository groupRepository;

    private final GroupUserJoinService groupUserJoinService;

    @Autowired
    public GroupService(UserService userService,
                        GroupRepository groupRepository,
                        GroupUserJoinService groupUserJoinService) {
        this.userService = userService;
        this.groupRepository = groupRepository;
        this.groupUserJoinService = groupUserJoinService;
    }

//    @Transactional
//    public void delete(Long groupId) {
//        groupRepository.deleteById(groupId);
//    }

    public GroupEntity findByName(String name) {
        return groupRepository.findByName(name);
    }

    public Long findIdByName(String name) {
        return groupRepository.findIdByName(name);
    }

    public GroupEntity createGroup(Long userId, String name, String type) {
        GroupUser groupUser = new GroupUser();
        GroupEntity group = new GroupEntity();
        group.setName(name);
        group.setGroupTypeEnum(GroupTypeEnum.valueOf(type.toUpperCase()));
        GroupEntity savedGroup = groupRepository.save(group);
        UserEntity user = userService.findById(userId);
        groupUser.setGroupId(savedGroup.getId());
        groupUser.setUserId(userId);
        groupUser.setRole(1);
        groupUser.setGroupMapping(group);
        groupUser.setUserMapping(user);
        groupUserJoinService.save(groupUser);
        return savedGroup;
    }

    public boolean checkIfGroupNameAlreadyUsed(String name) {
        return groupRepository.existsByName(name);
    }

}
