package net.sokontokoro_factory.lovelive.service;

import net.sokontokoro_factory.lovelive.PrivateField;
import net.sokontokoro_factory.lovelive.TestDatabase;
import net.sokontokoro_factory.lovelive.exception.InvalidArgumentException;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.persistence.entity.ScoreEntity;
import net.sokontokoro_factory.lovelive.type.GameType;
import net.sokontokoro_factory.lovelive.persistence.facade.ScoreFacade;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(CdiTestRunner.class)
public class ScoreServiceTest {
    @Inject
    private ScoreService targetClass;

    @Inject
    private ScoreFacade scoreFacade;

    @Test
    public void test_getScore_スコア情報を取得できる()throws Exception{
        long userId = 111111;
        ScoreEntity actualScore = targetClass.getScore(GameType.HONOCAR, userId);

        assertThat(actualScore.getUserId(),is(userId));
    }

    @Test
    public void test_getScore_ユーザーIDが存在しない場合例外が発生する()throws Exception{
        long notExistUserId = 25252;
        try{
            targetClass.getScore(GameType.HONOCAR, notExistUserId);
            fail();
        }catch (InvalidArgumentException ok){

        }
    }

    @Test
    public void test_getScore_スコアが未登録の場合例外が発生する()throws Exception{
        long notExistUserId = 999999;
        try{
            targetClass.getScore(GameType.SHAKARIN, notExistUserId);
            fail();
        }catch (NoResourceException ok){

        }
    }


    @Test
    public void test_getTop_ランキング上位のスコアリストを降順で取得できる()throws Exception{
        int listRenge = 10;
        List<ScoreEntity> actualList = targetClass.getList(GameType.HONOCAR, 1, listRenge);

        long count = 0;

        // 降順のリストである
        int preventPoint = Integer.MAX_VALUE;
        for (ScoreEntity score: actualList) {
            assertTrue(preventPoint >= score.getPoint());
            preventPoint = score.getPoint();
        }

        // 重複考慮したスコアリストの数を取得する
        preventPoint = Integer.MAX_VALUE;
        for (ScoreEntity score: actualList) {
            if(preventPoint != score.getPoint()){
                count ++;
            }
            preventPoint = score.getPoint();
        }

        // 重複考慮された、順位数分のリストを返却している
        long countOfRanking = actualList.stream().map(list -> list.getPoint()).distinct().count();
        assertThat(countOfRanking, is(count));
    }

    @Test
    public void test_getRanking_順位を取得できる()throws Exception{

        List<ScoreEntity> all = scoreFacade.findAll()
                .stream()
                .filter(score -> !score.getUserEntity().isDeleted())
                .filter(score -> score.getGame() == GameType.HONOCAR)
                .sorted(comparing(ScoreEntity::getPoint).reversed())
                .collect(Collectors.toList());

        // １位
        long topRanking = targetClass.getRankingNumber(GameType.HONOCAR, all.get(0).getPoint());
        assertThat(topRanking, is(1l));

        // 最下位
        long lowest = targetClass.getRankingNumber(GameType.HONOCAR, all.get(all.size() -1).getPoint());
        assertThat(lowest, is(Long.valueOf(all.size())));
    }


    /******************************************************
     * test management method
     */
    /** データベースのバックアップ */
    private File backupFile;

    @Before
    public void setUp() throws Exception {
        backupFile = TestDatabase.createBackupFile();
        TestDatabase.importTestDataSet();
    }

    @After
    public void tearDown() throws Exception {
        TestDatabase.importBackupFile(backupFile);
    }
}