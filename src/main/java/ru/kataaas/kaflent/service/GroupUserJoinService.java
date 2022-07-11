package ru.kataaas.kaflent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.entity.GroupRoleKey;
import ru.kataaas.kaflent.entity.GroupUser;
import ru.kataaas.kaflent.repository.GroupUserJoinRepository;

import java.util.List;

@Service
public class GroupUserJoinService {

    private final GroupUserJoinRepository groupUserJoinRepository;

    @Autowired
    public GroupUserJoinService(GroupUserJoinRepository groupUserJoinRepository) {
        this.groupUserJoinRepository = groupUserJoinRepository;
    }

    public GroupUser save(GroupUser groupUser) {
        return groupUserJoinRepository.save(groupUser);
    }

    public List<GroupUser> getAllByUserId(Long userId) {
        return groupUserJoinRepository.getAllByUserId(userId);
    }

    public List<Long> getUserIdsByGroupId(Long groupId) {
        return groupUserJoinRepository.getUserIdsInGroup(groupId);
    }

    public List<Long> getGroupIdsByUserId(Long userId) {
        return groupUserJoinRepository.getGroupIdsByUser(userId);
    }

    public GroupUser findByUserIdAndGroupId(Long userId, Long groupId) {
        return groupUserJoinRepository.findByUserIdAndGroupId(userId, groupId);
    }

    public void grantUserAdminInGroup(Long userId, Long groupId) {
        setUserRoleInGroup(userId, groupId, 1);
    }

    public void removeUserAdminFromGroup(Long userId, Long groupId) {
        setUserRoleInGroup(userId, groupId, 0);
    }

    private void setUserRoleInGroup(Long userId, Long groupId, int role) {
        GroupUser groupUser = groupUserJoinRepository.findByUserIdAndGroupId(userId, groupId);
        if (groupUser != null) {
            groupUser.setRole(role);
            groupUserJoinRepository.save(groupUser);
        }
    }

    public void removeUserFromGroup(Long userId, Long groupId) {
        groupUserJoinRepository.removeByUserIdAndGroupId(userId, groupId);
    }

    public int countUsersByGroup(Long groupId) {
        return groupUserJoinRepository.countAllByGroupId(groupId);
    }

    public boolean checkIfUserIsAuthorizedInGroup(Long userId, Long groupId) {
        return groupUserJoinRepository.existsByUserIdAndGroupIdAndApplicationAccepted(userId, groupId, true);
    }

    public boolean checkIfUserIsNonBannedInGroup(Long userId, Long groupId) {
        GroupUser groupUser = findByUserIdAndGroupId(userId, groupId);
        if (groupUser != null)
            return groupUser.isUserNonBanned();
        return true;
    }

}
