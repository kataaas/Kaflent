package ru.kataaas.kaflent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.entity.GroupEntity;
import ru.kataaas.kaflent.entity.GroupUser;
import ru.kataaas.kaflent.entity.UserEntity;
import ru.kataaas.kaflent.mapper.GroupMapper;
import ru.kataaas.kaflent.payload.GroupDTO;
import ru.kataaas.kaflent.payload.GroupMemberDTO;
import ru.kataaas.kaflent.payload.GroupResponse;
import ru.kataaas.kaflent.repository.GroupRepository;
import ru.kataaas.kaflent.utils.GroupTypeEnum;

import java.util.List;
import java.util.Optional;

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

    public Optional<GroupEntity> findById(Long id) {
        return groupRepository.findById(id);
    }

    public Optional<GroupEntity> findByName(String name) {
        return groupRepository.findByName(name);
    }

    public GroupResponse getGroupsByIds(List<Long> ids, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<GroupEntity> groups = groupRepository.findAllByIdInOrderByNameAsc(ids, pageable);
        return groupMapper.toGroupResponse(groups);
    }

    public GroupMemberDTO addUserToGroup(Long userId, Long groupId) {
        Optional<GroupEntity> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isPresent()) {
            if (!groupUserJoinService.checkIfUserIsAuthorizedInGroup(userId, groupId)) {
                UserEntity user = userService.findById(userId);
                GroupUser groupUser = new GroupUser();
                groupUser.setGroupMapping(optionalGroup.orElse(null));
                groupUser.setUserMapping(user);
                groupUser.setGroupId(groupId);
                groupUser.setUserId(userId);
                groupUser.setRole(0);
                groupUser.setUserNonBanned(true);
                if (optionalGroup.orElse(null).getGroupTypeEnum().equals(GroupTypeEnum.PUBLIC)) {
                    groupUser.setApplicationAccepted(true);
                    groupUser.setInGroup(true);
                    GroupUser savedGroupUser = groupUserJoinService.save(groupUser);
                    optionalGroup.orElse(null).getGroupUsers().add(savedGroupUser);
                    save(optionalGroup.orElse(null));
                    return new GroupMemberDTO(user.getUsername(), false, true, true);
                } else {
                    groupUser.setApplicationAccepted(false);
                    GroupUser savedGroupUser = groupUserJoinService.save(groupUser);
                    optionalGroup.orElse(null).getGroupUsers().add(savedGroupUser);
                    save(optionalGroup.orElse(null));
                }
                return new GroupMemberDTO(user.getUsername(), false, false, false);
            }
        }
        return null;
    }

    public GroupDTO createGroup(Long userId, String name, String type) {
        GroupUser groupUser = new GroupUser();
        UserEntity user = userService.findById(userId);
        GroupEntity group = new GroupEntity();
        group.setName(name);
        group.setGroupTypeEnum(GroupTypeEnum.valueOf(type.toUpperCase()));
        GroupEntity savedGroup = groupRepository.save(group);

        groupUser.setGroupId(savedGroup.getId());
        groupUser.setUserId(userId);
        groupUser.setRole(1);
        groupUser.setGroupMapping(group);
        groupUser.setUserMapping(user);
        groupUser.setInGroup(true);
        groupUser.setApplicationAccepted(true);
        groupUser.setUserNonBanned(true);
        savedGroup.getUserEntities().add(user);
        groupUserJoinService.save(groupUser);

        return groupMapper.toGroupDTO(savedGroup);
    }

    public boolean checkIfGroupNameAlreadyUsed(String name) {
        return groupRepository.existsByName(name);
    }

}
