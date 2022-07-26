package ru.kataaas.kaflent.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "group_user")
@IdClass(GroupRoleKey.class)
public class GroupUser {

    @Id
    private Long groupId;

    @Id
    private Long userId;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    GroupEntity groupMapping;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    UserEntity userMapping;

    private int role;

    private boolean inGroup;

    private boolean userNonBanned;

    private boolean applicationAccepted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupUser groupUser = (GroupUser) o;
        return Objects.equals(groupId, groupUser.groupId) && Objects.equals(userId, groupUser.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, userId);
    }
}
