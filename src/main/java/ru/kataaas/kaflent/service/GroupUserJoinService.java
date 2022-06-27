package ru.kataaas.kaflent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public int countUsersByGroup(Long groupId) {
        return groupUserJoinRepository.countAllByGroupId(groupId);
    }

    public boolean checkIfUserIsAuthorizedInGroup(Long userId, Long groupId) {
        return groupUserJoinRepository.existsByUserIdAndGroupId(userId, groupId);
    }

}
