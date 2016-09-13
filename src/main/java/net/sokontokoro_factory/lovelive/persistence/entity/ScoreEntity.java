package net.sokontokoro_factory.lovelive.persistence.entity;

import lombok.Data;
import net.sokontokoro_factory.lovelive.type.GameType;

import javax.persistence.*;

@Entity
@Table(name = "SCORE")
@Data
public class ScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "GAME", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameType game;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "POINT", nullable = false)
    private Integer point;

    @Column(name = "CREATE_DATE", nullable = false)
    private Long createDate;

    @Column(name = "UPDATE_DATE")
    private Long updateDate;

    @Column(name = "FINAL_DATE", nullable = false)
    private Long finalDate;

    @Column(name = "COUNT", nullable = false)
    private Integer count;

    /*************************************
     * relation
     */

    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne
    private UserEntity userEntity;
}
