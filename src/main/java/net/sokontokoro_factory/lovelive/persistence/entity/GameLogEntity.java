package net.sokontokoro_factory.lovelive.persistence.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "GAME_LOG")
@Data
public class GameLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "GAME_ID", nullable = false)
    private Integer gameId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "POINT", nullable = false)
    private Integer point;

    @Column(name = "PLAY_DATE", nullable = false)
    private Long playDate;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "START_SESSION_DATE")
    private Long startSessionDate;

    @Column(name = "CLIENT_IP")
    private String clientIp;

    @Column(name = "USER_AGENT")
    private String userAgent;

    @Column(name = "LOCALE")
    private String locale;
}