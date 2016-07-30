package net.sokontokoro_factory.lovelive.service;

import net.sokontokoro_factory.lovelive.persistence.entity.GameLogEntity;
import net.sokontokoro_factory.lovelive.persistence.facade.GameLogFacade;
import net.sokontokoro_factory.lovelive.persistence.master.MasterGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.sessions.Login;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequestScoped
public class LogService {
    private static final Logger logger = LogManager.getLogger(LogService.class);

    @Inject
    GameLogFacade gameLogFacade;

//    @Inject
//    LoginSession loginSession;

    /**
     * ゲームログを登録する
     *
     * @param game
     * @param userId
     * @param point
     */
    public void addGameLog(
            MasterGame game,
            long userId,
            int point){
        logger.entry("add()", game, userId);

        GameLogEntity log = new GameLogEntity();
        log.setGameId(game.getId());
        log.setUserId(userId);
        log.setPoint(point);
        log.setPlayDate(System.currentTimeMillis());
//        log.setSessionId(loginSession.getRequest().getRequestedSessionId());
//        log.setStartSessionDate(loginSession.getRequest().getSession().getCreationTime());
//        log.setClientIp(loginSession.getRequest().getRemoteAddr());
//        log.setUserAgent(loginSession.getRequest().getHeader("user-agent"));
//        log.setLocale(loginSession.getRequest().getLocale().toString());

        try{
            gameLogFacade.beginTransaction();
            gameLogFacade.create(log);
            gameLogFacade.commit();
        }catch (PersistenceException e){
            logger.catching(e);

            if(gameLogFacade.isActive()){
                gameLogFacade.rollback();
            }
        }
        logger.traceExit();
    }
}
