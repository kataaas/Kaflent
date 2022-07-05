package ru.kataaas.kaflent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kataaas.kaflent.entity.GroupEntity;
import ru.kataaas.kaflent.entity.GroupUser;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.mapper.GroupMapper;
import ru.kataaas.kaflent.payload.GroupResponse;
import ru.kataaas.kaflent.repository.GroupRepository;
import ru.kataaas.kaflent.utils.GroupTypeEnum;

import java.util.List;

@Service
public class GroupService {

    private final UserService userService;

    private final GroupMapper groupMapper;

    private final GroupRepository groupRepository;

    private final GroupUserJoinService groupUserJoinService;

    @Autowired
    public GroupService(UserService userService,
                        GroupMapper groupMapper,
                        GroupRepository groupRepository,
                        GroupUserJoinService groupUserJoinService) {
        this.userService = userService;
        this.groupMapper = groupMapper;
        this.groupRepository = groupRepository;
        this.groupUserJoinService = groupUserJoinService;
    }

    public void save(GroupEntity group) {
        groupRepository.save(group);
    }

    public Long findIdByName(String name) {
        return groupRepository.findIdByName(name);
    }

    public GroupEntity findById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

    public GroupEntity findByName(String name) {
        return groupRepository.findByName(name);
    }

    public GroupResponse getGroupsByIds(List<Long> ids, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<GroupEntity> groups = groupRepository.findAllByIdInOrderByNameAsc(ids, pageable);
        return groupMapper.toGroupResponse(groups);
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
