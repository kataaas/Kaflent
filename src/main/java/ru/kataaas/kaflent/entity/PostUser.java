package ru.kataaas.kaflent.entity;

import lombok.Getter;
import lombok.Setter;
import ru.kataaas.kaflent.utils.EmotionsTypeEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "post_emotions")
@IdClass(PostUserKey.class)
public class PostUser implements Serializable {

    @Id
    private Long postId;

    @Id
    private Long userId;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    PostEntity postMapping;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    UserEntity userMapping;

    @Column(name = "emotion")
    @Enumerated(value = EnumType.STRING)
    private EmotionsTypeEnum emotion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostUser postUser = (PostUser) o;
        return Objects.equals(postId, postUser.postId) && Objects.equals(userId, postUser.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userId);
    }

}
