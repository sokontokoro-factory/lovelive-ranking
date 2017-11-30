package net.sokontokoro_factory.lovelive.persistence.entity;

import lombok.Data;
import net.sokontokoro_factory.lovelive.type.GameType;

import javax.persistence.*;

/**
 * TODO: スコアの持ち方の再検討
 * IntegerのPointカラムしかないため、少数、文字列の評価ができない。
 * おいものみきりから、レベル、x人抜きの点数以外の概念が出たため、汎用的に格納できるデータの持ち方にする必要がある。
 *
 * おいものみきりの点数について
 * ベストタイム最大五桁、レベル1桁、人抜き1桁の 3-7桁でスコアを格納する
 * 1: 易しい 2: 普通 3: 難しい
 * 9999910の場合、ベストタイムは99999、レベルは易しい、0人抜き
 * 23335の場合、ベストタイムは233、レベルは難しい、5人抜き
 */
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
