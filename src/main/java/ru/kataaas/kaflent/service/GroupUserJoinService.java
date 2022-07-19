package ru.kataaas.kaflent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kataaas.kaflent.entity.GroupRoleKey;
import ru.kataaas.kaflent.entity.GroupUser;
import ru.kataaas.kaflent.repository.GroupUserJoinRepository;

import java.math.BigInteger;
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

    @Transactional
    public void deleteByUserIdAndGroupId(Long userId, Long groupId) {
        groupUserJoinRepository.deleteByUserIdAndGroupId(userId, groupId);
    }

    public List<GroupUser> getAllByUserId(Long userId) {
        return groupUserJoinRepository.getAllByUserId(userId);
    }

    public Page<BigInteger> getUserIdsByGroupId(Long groupId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return groupUserJoinRepository.getUserIdsInGroup(groupId, pageable);
    }

    public Page<BigInteger> getUserIdsByRequestToGroup(Long groupId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return groupUserJoinRepository.getUserIdsByRequestToGroup(groupId, pageable);
    }

    public Page<BigInteger> getBannedUserIdsInGroup(Long groupId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return groupUserJoinRepository.getBannedUserIdsInGroup(groupId, pageable);
    }

    public Page<BigInteger> getGroupIdsByUserId(Long userId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return groupUserJoinRepository.getGroupIdsByUser(userId, pageable);
    }

    public GroupUser findByUserIdAndGroupId(Long userId, Long groupId) {
        return groupUserJoinRepository.findByUserIdAndGroupId(userId, groupId);
    }

    public void banUserInGroup(Long userId, Long groupId) {
        setUserBannedInGroup(userId, groupId, false);
    }

    public void unbanUserInGroup(Long userId, Long groupId) {
        setUserBannedInGroup(userId, groupId, true);
    }

    private void setUserBannedInGroup(Long userId, Long groupId, boolean accountNonBanned) {
        GroupUser groupUser = groupUserJoinRepository.findByUserIdAndGroupId(userId, groupId);
        if (groupUser != null) {
            groupUser.setUserNonBanned(accountNonBanned);
            groupUserJoinRepository.save(groupUser);
        }
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
        GroupUser groupUser = findByUserIdAndGroupId(userId, groupId);
        if (groupUser != null) {
            groupUser.setInGroup(false);
            save(groupUser);
        }
    }

    public void acceptUserToGroup(Long userId, Long groupId) {
        GroupUser groupUser = findByUserIdAndGroupId(userId, groupId);
        if (groupUser != null) {
            groupUser.setApplicationAccepted(true);
            groupUser.setInGroup(true);
            save(groupUser);
        }
    }

    public int countUsersByGroup(Long groupId) {
        return groupUserJoinRepository.countAllByGroupId(groupId);
    }

    public boolean checkIfUserIsAuthorizedInGroup(Long userId, Long groupId) {
        return groupUserJoinRepository.existsByUserIdAndGroupIdAndInGroup(userId, groupId, true);
    }

    public boolean checkIfUserIsNonBannedInGroup(Long userId, Long groupId) {
        GroupUser groupUser = findByUserIdAndGroupId(userId, groupId);
        if (groupUser != null)
            return groupUser.isUserNonBanned();
        return true;
    }

}
