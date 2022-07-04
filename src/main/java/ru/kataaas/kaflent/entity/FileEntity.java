package ru.kataaas.kaflent.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import ru.kataaas.kaflent.utils.FileTypeEnum;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "file_storage")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String url;

//    private Long ownerId; // user id or group id

    @Enumerated(value = EnumType.STRING)
    private FileTypeEnum type;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

}
