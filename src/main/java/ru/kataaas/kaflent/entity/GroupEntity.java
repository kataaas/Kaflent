package ru.kataaas.kaflent.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import ru.kataaas.kaflent.utils.GroupTypeEnum;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "groups_entities")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imageName;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    private GroupTypeEnum groupTypeEnum;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_user",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> userEntities = new HashSet<>();

    @OneToMany(mappedBy = "groupMapping", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<GroupUser> groupUsers = new HashSet<>();

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupEntity group = (GroupEntity) o;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
