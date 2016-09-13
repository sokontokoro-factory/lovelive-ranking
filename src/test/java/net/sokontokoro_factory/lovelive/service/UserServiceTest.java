package net.sokontokoro_factory.lovelive.service;

import net.sokontokoro_factory.lovelive.TestDatabase;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.persistence.entity.UserEntity;
import net.sokontokoro_factory.lovelive.persistence.facade.UserFacade;
import net.sokontokoro_factory.lovelive.type.FavoriteType;
import net.sokontokoro_factory.tweetly_oauth.dto.AccessToken;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(CdiTestRunner.class)
public class UserServiceTest {
    @Inject
    private UserService userService;

    @Inject
    private UserFacade userFacade;

    private static final String TEST_USER_ACCESS_TOKEN = "298062670-V47MZpkeszBx9aWEYmYRPTCTLjHFKWudAUNc7d05";
    private static final String TEST_USER_ACCESS_TOKEN_SECRET = "TUODpJhOSDajAortXlKDgaWK0NqgdkcqGrDGAcTbWJ3iy";
    private static final String TEST_USER_ID = "298062670";
    private static final String TEST_USER_ICON_IMAGE_URL = "http://pbs.twimg.com/profile_images/3144229927/1ebde74324e5fb5d91ec6aa9bc3024fa_normal.gif";

    @Test
    public void test_getProfileImageUrl_プロファイル画像のURLを取得できる() throws Exception {
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(TEST_USER_ACCESS_TOKEN);
        accessToken.setTokenSecret(TEST_USER_ACCESS_TOKEN_SECRET);
        String url = userService.getProfileImageUrl(Long.parseLong(TEST_USER_ID), accessToken);

        assertThat(url, is(TEST_USER_ICON_IMAGE_URL));
    }

    @Test
    public void test_getById_ID検索ができる() throws Exception{
        long userId = 111111;
        UserEntity actualUser = userService.getById(userId);
        assertThat(actualUser.getId(), is(userId));
    }

    @Test
    public void test_getById_存在しない場合例外が発生する(){
        long notExistUserId = 25252;

        try{
            userService.getById(notExistUserId);
            fail();
        }catch (NoResourceException ok){

        }
    }

    @Test
    public void test_getById_論理削除されている場合例外が発生する(){
        long deletedUserId = 444444;

        try{
            userService.getById(deletedUserId);
            fail();
        }catch (NoResourceException ok){

        }
    }

    @Test
    public void test_create_新規作成できる()throws Exception{
        long createUserId = 999999;
        String createUserName = "name";
        userService.create(createUserId, createUserName);

        UserEntity createdUser = userFacade.findById(createUserId);

        assertThat(createdUser.getId(), is(createUserId));
        assertThat(createdUser.getName(), is(createUserName));
    }


    @Test
    public void test_create_論理削除済みのユーザーの削除フラグを消すことが出来る()throws Exception{
        long deletedUserId = 444444;
        String createUserName = "name";

        // 実行
        userService.create(deletedUserId, createUserName);

        // 削除フラグがfalseになっている
        UserEntity undeletedUser = userFacade.findById(deletedUserId);
        assertTrue(!undeletedUser.isDeleted());
    }


    @Test
    public void test_update_ユーザーが存在しない場合例外が発生する(){
        long notExistUserId = 25252;
        String userName = "any string";
        FavoriteType favorite = FavoriteType.KOTORI;

        // 実行
        try{
            userService.update(notExistUserId, userName, favorite);
            fail();
        }catch (NoResourceException ok){

        }
    }


    @Test
    public void test_delete_ユーザーを論理削除できる() throws Exception {
        long existUserId = 111111;

        // 削除されていないユーザー
        UserEntity existUser = userFacade.findById(existUserId);
        assertTrue(!existUser.isDeleted());

        // 実行
        userService.delete(existUserId);

        // 削除フラグがtrueになっている
        UserEntity deletedUser = userFacade.findById(existUserId);
        assertTrue(deletedUser.isDeleted());
    }


    @Test
    public void test_delete_ユーザーが存在しない場合例外が発生する(){
        long notExistUserId = 25252;

        // 実行
        try{
            userService.delete(notExistUserId);
            fail();
        }catch (NoResourceException ok){

        }
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