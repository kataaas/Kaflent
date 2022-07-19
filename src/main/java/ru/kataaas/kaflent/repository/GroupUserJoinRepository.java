package ru.kataaas.kaflent.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kataaas.kaflent.entity.GroupRoleKey;
import ru.kataaas.kaflent.entity.GroupUser;

import java.math.BigInteger;
import java.util.List;

public interface GroupUserJoinRepository extends JpaRepository<GroupUser, GroupRoleKey> {

    GroupUser findByUserIdAndGroupId(Long userId, Long groupId);

    List<GroupUser> getAllByGroupId(Long groupId);

    List<GroupUser> getAllByUserId(Long userId);

    @Query(value = "SELECT g.user_id FROM group_user g WHERE group_id = :groupId AND in_group = true AND user_non_banned = true", nativeQuery = true)
    Page<BigInteger> getUserIdsInGroup(@Param("groupId") Long groupId, Pageable pageable);

    @Query(value = "SELECT g.user_id FROM group_user g WHERE group_id = :groupId AND in_group = false AND user_non_banned = true", nativeQuery = true)
    Page<BigInteger> getUserIdsByRequestToGroup(@Param("groupId") Long groupId, Pageable pageable);

    @Query(value = "SELECT g.user_id FROM group_user g WHERE group_id = :groupId AND user_non_banned = false", nativeQuery = true)
    Page<BigInteger> getBannedUserIdsInGroup(@Param("groupId") Long groupId, Pageable pageable);

    @Query(value = "SELECT g.group_id FROM group_user g WHERE user_id = :userId AND in_group = true AND user_non_banned = true", nativeQuery = true)
    Page<BigInteger> getGroupIdsByUser(@Param("userId") Long userId, Pageable pageable);

    int countAllByGroupId(Long groupId);

    boolean existsByUserIdAndGroupIdAndInGroup(Long userId, Long groupId, boolean inGroup);

    void deleteAllByGroupId(Long groupId);

    void deleteByUserIdAndGroupId(Long userId, Long groupId);

}
