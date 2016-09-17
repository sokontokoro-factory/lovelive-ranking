package net.sokontokoro_factory.lovelive.service;

import net.sokontokoro_factory.lovelive.exception.InvalidArgumentException;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.persistence.entity.ScoreEntity;
import net.sokontokoro_factory.lovelive.persistence.facade.GameLogFacade;
import net.sokontokoro_factory.lovelive.type.GameType;
import net.sokontokoro_factory.lovelive.persistence.facade.ScoreFacade;
import net.sokontokoro_factory.lovelive.persistence.facade.UserFacade;
import net.sokontokoro_factory.yoshinani.file.config.Config;
import net.sokontokoro_factory.yoshinani.file.config.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

@RequestScoped
public class ScoreService {
    private static final Logger logger = LogManager.getLogger(ScoreService.class);

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
    public ScoreEntity getScore(GameType game, long userId) throws NoResourceException, InvalidArgumentException {
        /* ID確認 */
        if(!userFacade.isExist(userId)){
            throw new InvalidArgumentException("存在しないユーザーIDです。");
        }

        ScoreEntity score = scoreFacade.findOne(game, userId);
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
    @Transactional
    public void insertScore(GameType game, long userId, int point) throws InvalidArgumentException {

        /* ID確認 */
        if(!userFacade.isExist(userId)){
            throw new InvalidArgumentException("存在しないユーザーIDです。");
        }

        ScoreEntity registeredScore = scoreFacade.findOne(game, userId);

        if (registeredScore != null){
            update(registeredScore, point);
        } else {
            insert(game, userId, point);
        }
    }

    /**
     * 順位を取得する
     *
     * @param targetGame    対象のゲーム
     * @param targetPoint   順位を取得する点数
     * @return
     */
    public Long getRankingNumber(GameType targetGame, int targetPoint){
        logger.entry("getRanking()", targetGame, targetPoint);

        List<ScoreEntity> allScore = scoreFacade.findAll();
        long ranking = allScore
                .stream()
                .filter(score -> !score.getUserEntity().isDeleted())
                .filter(score -> score.getGame() == targetGame)
                .filter(score -> score.getPoint() > targetPoint)
                .count() + 1;

        return logger.traceExit(ranking);
    }

    /**
     * offsetRankingNumber以下の順位のScoreEntityのリストを取得する
     *
     * @param targetGame            対象のゲーム
     * @param offsetRankingNumber   検索するリストのスコアの上限値
     * @param range                 検索するリストの数
     * @return
     */
    public List<ScoreEntity> getList(GameType targetGame, int offsetRankingNumber, int range){
        logger.entry(targetGame, offsetRankingNumber);
        int offsetBorderPoint = getBorderPoint(targetGame, offsetRankingNumber);
        int limitBorderPoint = getBorderPoint(targetGame, offsetRankingNumber + range);

        List<ScoreEntity> allScore = scoreFacade.findAll();
        List<ScoreEntity> topScores = allScore
                .stream()
                .filter(score -> !score.getUserEntity().isDeleted())
                .filter(score -> score.getGame() == targetGame)
                .filter(score -> score.getPoint() <= offsetBorderPoint)
                .filter(score -> score.getPoint() > limitBorderPoint)
                .sorted(comparing(ScoreEntity::getPoint).reversed())
                .collect(Collectors.toList());

        return logger.traceExit(topScores);
    }

    /**
     * 指定のランキング順位に入るための最低ポイントを取得する
     *
     * @param targetGame
     * @param targetRankingNumber
     * @return
     */
    public int getBorderPoint(GameType targetGame, int targetRankingNumber){
        logger.entry(targetGame);
        List<ScoreEntity> allScore = scoreFacade.findAll();

        List<Integer> descPoints = allScore
                .stream()
                .filter(score -> score.getGame() == targetGame)
                .map(score -> score.getPoint())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // スコア未登録の場合
        if(descPoints.size() == 0){
            return 0;
        }

        int borderPoint;
        if(descPoints.size() < targetRankingNumber){
            borderPoint = descPoints.get(descPoints.size() - 1);
        }else {
            borderPoint = descPoints.get(targetRankingNumber - 1);
        }
        logger.info("game: " + targetGame + ", borderpoint: " + borderPoint);
        return logger.traceExit(borderPoint);
    }

    /*----------------- ↓ private method ↓ ---------------------*/

    /**
     * スコアを新規登録する。
     * gameName、userIdの組み合わせが既存で無いことを確認すること
     *
     * @param game
     * @param userId
     * @param point
     */
    private void insert(GameType game, long userId, int point){
        logger.entry(game, userId, point);

        ScoreEntity scoreEntity = new ScoreEntity();
        scoreEntity.setGame(game);
        scoreEntity.setUserId(userId);
        scoreEntity.setPoint(point);
        scoreEntity.setCount(1);
        scoreEntity.setCreateDate(System.currentTimeMillis());
        scoreEntity.setFinalDate(System.currentTimeMillis());

        scoreFacade.create(scoreEntity);
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

        if(point > scoreEntity.getPoint()){
            scoreEntity.setPoint(point);
            scoreEntity.setUpdateDate(System.currentTimeMillis());
        }
        scoreEntity.setCount(scoreEntity.getCount() + 1);
        scoreEntity.setFinalDate(System.currentTimeMillis());

        logger.traceExit();
    }
}
