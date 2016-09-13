package net.sokontokoro_factory.lovelive.persistence.entity;

import lombok.Data;
import net.sokontokoro_factory.lovelive.type.FavoriteType;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "USER")
@Data
public class UserEntity{
    @Id
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "CREATE_DATE", nullable = false)
    private Long createDate;

    @Column(name = "UPDATE_DATE", nullable = true)
    private Long updateDate;

    @Column(name = "FAVORITE", nullable = true)
    @Enumerated(EnumType.STRING)
    private FavoriteType favorite;

    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    @Column(name = "ADMIN", nullable = false)
    private boolean admin;

    /*************************************
     * relation
     */

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userEntity")
    private List<ScoreEntity> scoreEntities;
}
