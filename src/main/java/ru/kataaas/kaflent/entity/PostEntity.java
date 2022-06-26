package ru.kataaas.kaflent.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "posts")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Long groupId;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

}
