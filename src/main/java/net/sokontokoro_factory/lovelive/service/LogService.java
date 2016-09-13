package net.sokontokoro_factory.lovelive.service;

import net.sokontokoro_factory.lovelive.persistence.entity.GameLogEntity;
import net.sokontokoro_factory.lovelive.persistence.facade.GameLogFacade;
import net.sokontokoro_factory.lovelive.type.GameType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@RequestScoped
public class LogService {
    private static final Logger logger = LogManager.getLogger(LogService.class);

    @Inject
    GameLogFacade gameLogFacade;

    @Inject
    LoginSession loginSession;

    /**
     * ゲームログを登録する
     *
     * @param game
     * @param userId
     * @param point
     */
    @Transactional
    public void addGameLog(
            GameType game,
            Long userId,
            int point){
        logger.entry("add()", game, userId);

        GameLogEntity log = new GameLogEntity();
        log.setGame(game);
        log.setUserId(userId);
        log.setPoint(point);
        log.setPlayDate(System.currentTimeMillis());
        log.setSessionId(loginSession.getRequest().getRequestedSessionId());
        log.setStartSessionDate(loginSession.getRequest().getSession().getCreationTime());
        log.setClientIp(loginSession.getRequest().getRemoteAddr());
        log.setUserAgent(loginSession.getRequest().getHeader("user-agent"));
        log.setLocale(loginSession.getRequest().getLocale().toString());

        gameLogFacade.create(log);

        logger.traceExit();
    }
}
