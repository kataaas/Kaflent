package ru.kataaas.kaflent.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    private String image;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "userEntities", cascade = CascadeType.ALL)
    private Set<GroupEntity> groups = new HashSet<>();

    @OneToMany(mappedBy = "groupMapping", fetch = FetchType.EAGER)
    private Set<GroupUser> groupUsers = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @Column(name = "enabled")
    private boolean enabled;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity user = (UserEntity) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
