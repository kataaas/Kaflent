package ru.kataaas.kaflent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kataaas.kaflent.entity.GroupRoleKey;
import ru.kataaas.kaflent.entity.GroupUser;

import java.util.List;

public interface GroupUserJoinRepository extends JpaRepository<GroupUser, GroupRoleKey> {

    GroupUser findByUserIdAndGroupId(Long userId, Long groupId);

    List<GroupUser> getAllByGroupId(Long groupId);

    List<GroupUser> getAllByUserId(Long userId);

    @Query(value = "SELECT g.user_id FROM group_user g WHERE group_id = :groupId AND application_accepted = true AND user_non_banned = true", nativeQuery = true)
    List<Long> getUserIdsInGroup(@Param("groupId") Long groupId);

    @Query(value = "SELECT g.group_id FROM group_user g WHERE user_id = :userId AND application_accepted = true AND user_non_banned = true", nativeQuery = true)
    List<Long> getGroupIdsByUser(@Param("userId") Long userId);

    void removeByUserIdAndGroupId(Long userId, Long groupId);

    int countAllByGroupId(Long groupId);

    boolean existsByUserIdAndGroupIdAndApplicationAccepted(Long userId, Long groupId, boolean accepted);

    void deleteAllByGroupId(Long groupId);
}
