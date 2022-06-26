package ru.kataaas.kaflent.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String name;

}
