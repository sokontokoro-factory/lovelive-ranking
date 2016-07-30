package net.sokontokoro_factory.lovelive.service;

import net.sokontokoro_factory.lovelive.exception.InvalidArgumentException;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.persistence.entity.ScoreEntity;
import net.sokontokoro_factory.lovelive.persistence.facade.GameLogFacade;
import net.sokontokoro_factory.lovelive.persistence.master.MasterGame;
import net.sokontokoro_factory.lovelive.persistence.facade.ScoreFacade;
import net.sokontokoro_factory.lovelive.persistence.facade.UserFacade;
import net.sokontokoro_factory.yoshinani.file.config.Config;
import net.sokontokoro_factory.yoshinani.file.config.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@RequestScoped
public class ScoreService {
    private static final Logger logger = LogManager.getLogger(ScoreService.class);

    private static final Config cofig = ConfigLoader.getProperties("config");
    private static final int PRODUCE_NUMBER_OF_RANKING = cofig.getInt("produce.number.ranking");

    @Inject
    ScoreFacade scoreFacade;

    @Inject
    UserFacade userFacade;

    @Inject
    GameLogFacade gameLogFacade;

    /**
     * スコア情報を返す
     *
     * @param game
     * @param userId
     * @return
     * @throws NoResourceException          スコアが存在しないとき
     * @throws InvalidArgumentException     ユーザーIDが存在しないとき
     */
    public ScoreEntity getScore(MasterGame game, long userId) throws NoResourceException, InvalidArgumentException {
        /* ID確認 */
        if(!userFacade.isExist(userId)){
            throw new InvalidArgumentException("存在しないユーザーIDです。");
        }

        ScoreEntity score = scoreFacade.findById(game.getId(), userId);
        if(score == null){
            throw new NoResourceException("スコア未登録です。");
        }else{
            return score;
        }
    }

    /**
     * スコア情報をupsertする
     *
     * @param game
     * @param userId
     * @param point
     * @throws InvalidArgumentException
     */
    public void insertScore(MasterGame game, long userId, int point) throws InvalidArgumentException {

        /* ID確認 */
        if(!userFacade.isExist(userId)){
            throw new InvalidArgumentException("存在しないユーザーIDです。");
        }

        ScoreEntity registeredScore = scoreFacade.findById(game.getId(), userId);

        if (registeredScore != null){
            update(registeredScore, point);
        } else {
            insert(game.getId(), userId, point);
        }
    }

    /**
     * 順位を取得する
     *
     * @param game
     * @param targetPoint
     * @return
     */
    public Long getRankingNumber(MasterGame game, int targetPoint){
        logger.entry("getRanking()", game, targetPoint);

        List<ScoreEntity> allScore = scoreFacade.findAll();
        long ranking = allScore
                .stream()
                .filter(score -> score.getGameId() == game.getId())
                .filter(score -> score.getPoint() > targetPoint)
                .count() + 1;

        return logger.traceExit(ranking);
    }



    public List<ScoreEntity> getTops(MasterGame game, int offsetRankingNumber){
        logger.entry(game, offsetRankingNumber);
        int offsetBorderPoint = getBorderPoint(game, offsetRankingNumber);
        int limitBorderPoint = getBorderPoint(game, offsetRankingNumber + PRODUCE_NUMBER_OF_RANKING);

        List<ScoreEntity> allScore = scoreFacade.findAll();
        List<ScoreEntity> topScores = allScore
                .stream()
                .filter(score -> score.getGameId() == game.getId())
                .filter(score -> score.getPoint() <= offsetBorderPoint)
                .filter(score -> score.getPoint() > limitBorderPoint)
                .sorted(comparing(ScoreEntity::getPoint).reversed())
                .collect(Collectors.toList());

        return logger.traceExit(topScores);
    }

    public int getBorderPoint(MasterGame game, int targetRankingNumber){
        logger.entry(game);
        List<ScoreEntity> allScore = scoreFacade.findAll();

        List<Integer> descPoints = allScore
                .stream()
                .filter(score -> score.getGameId() == game.getId())
                .map(score -> score.getPoint())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        int borderPoint;
        if(descPoints.size() < targetRankingNumber){
            borderPoint = descPoints.get(descPoints.size() - 1);
        }else {
            borderPoint = descPoints.get(targetRankingNumber - 1);
        }
        logger.info("game: " + game + ", borderpoint: " + borderPoint);
        return logger.traceExit(borderPoint);
    }

    /*----------------- ↓ private method ↓ ---------------------*/

    /**
     * スコアを新規登録する。
     * gameName、userIdの組み合わせが既存で無いことを確認すること
     *
     * @param gameId
     * @param userId
     * @param point
     */
    private void insert(int gameId, long userId, int point){
        logger.entry(gameId, userId, point);

        ScoreEntity scoreEntity = new ScoreEntity();
        scoreEntity.setGameId(gameId);
        scoreEntity.setUserId(userId);
        scoreEntity.setPoint(point);
        scoreEntity.setCount(1);
        scoreEntity.setCreateDate(System.currentTimeMillis());
        scoreEntity.setFinalDate(System.currentTimeMillis());

        try{
            scoreFacade.beginTransaction();
            scoreFacade.create(scoreEntity);
            scoreFacade.commit();
        }catch (PersistenceException e){
            logger.catching(e);

            if(scoreFacade.isActive()){
                scoreFacade.rollback();
            }
        }
        logger.traceExit();
    }

    /**
     * スコアを更新する
     * @param scoreEntity
     * @param point
     * @return
     */
    private void update(ScoreEntity scoreEntity, int point){
        logger.entry(scoreEntity, point);

        try{
            scoreFacade.beginTransaction();

            if(point > scoreEntity.getPoint()){
                scoreEntity.setPoint(point);
                scoreEntity.setUpdateDate(System.currentTimeMillis());
            }
            scoreEntity.setCount(scoreEntity.getCount() + 1);
            scoreEntity.setFinalDate(System.currentTimeMillis());

            scoreFacade.commit();
        }catch (PersistenceException e){
            logger.catching(e);

            if(scoreFacade.isActive()){
                scoreFacade.rollback();
            }
        }
        logger.traceExit();
    }
}
