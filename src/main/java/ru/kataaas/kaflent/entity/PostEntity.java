package ru.kataaas.kaflent.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "posts")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Long groupId;

    @OneToMany
    @Size(max = 9)
    private Set<FileEntity> files = new HashSet<>();

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

}
