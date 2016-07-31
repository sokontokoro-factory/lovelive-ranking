package net.sokontokoro_factory.lovelive.service;

import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.persistence.entity.UserEntity;
import net.sokontokoro_factory.lovelive.persistence.facade.UserFacade;
import net.sokontokoro_factory.lovelive.persistence.master.MasterFavorite;
import net.sokontokoro_factory.tweetly_oauth.TweetlyOAuth;
import net.sokontokoro_factory.tweetly_oauth.TweetlyOAuthException;
import net.sokontokoro_factory.tweetly_oauth.dto.AccessToken;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

@RequestScoped
public class UserService{
    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    UserFacade userFacade;

    public String getProfileImageUrl(long userId, AccessToken accessToken) throws TweetlyOAuthException {
        logger.entry(userId, accessToken);

        /* twitterサーバーへの問い合わせ */
        TweetlyOAuth tweetlyOAuth = new TweetlyOAuth();
        String userProfile = userProfile = tweetlyOAuth.getUsersShow(String.valueOf(userId), accessToken);

        String profileImageUrl = new JSONObject(userProfile).get("profile_image_url").toString();

        return logger.traceExit(profileImageUrl);
    }

    /**
     * 論理削除されていないユーザーのID検索を行う
     *
     * @param userId
     * @return
     * @throws NoResourceException 存在しない、または論理削除済みの場合
     */
    public UserEntity getById(long userId) throws NoResourceException{
        logger.entry(userId);

        UserEntity user = userFacade.findById(userId);
        if(user == null){
            throw new NoResourceException("指定されたIDは未登録です。");
        }else if(user.getDeleted().equals(UserEntity.DELETED.TRUE.getValue())){
            throw new NoResourceException("削除済みのユーザーです。");
        }else{
            return logger.traceExit(user);
        }
    }

    /**
     * userを新規作成する。
     * 論理削除済みのユーザーの場合、deleteフラグの削除とuserNameの更新を行う
     *
     * @param userId
     * @param name
     * @throws PersistenceException 永続化に失敗した場合
     */
    public void create(long userId, String name) throws PersistenceException{
        logger.entry(userId, name);

        UserEntity user = userFacade.findById(userId);

        try{
            userFacade.beginTransaction();

            if(user != null){
                /* 既存レコードのため、論理削除を外す */
                user.setName(name);
                user.setDeleted(UserEntity.DELETED.FALSE.getValue());
            }else{
                /* レコード新規作成 */
                UserEntity createUser = new UserEntity();
                createUser.setId(userId);
                createUser.setName(name);
                createUser.setCreateDate(System.currentTimeMillis());
                createUser.setDeleted(UserEntity.DELETED.FALSE.getValue());
                createUser.setAdmin(UserEntity.ADMIN.FALSE.getValue());
                userFacade.create(createUser);
            }
            userFacade.commit();
        }catch (PersistenceException e){
            logger.catching(Level.ERROR, e);

            if(userFacade.isActive()){
                userFacade.rollback();
            }
            throw e;
        }
        logger.traceExit();
    }

    /**
     * user情報を更新する。nullまたは空文字の項目は更新しない
     * 更新対象：ユーザー名、論理削除フラグ
     *
     * @param userId                ユーザーID
     * @param name
     * @param favorite
     * @throws NoResourceException  ユーザーIDが存在しない場合
     */
    public void update(
            long userId,
            String name,
            MasterFavorite favorite) throws NoResourceException{

        logger.entry(userId, name, favorite);

        /* 更新対象のuser objectを取得 */
        UserEntity updateUser = getById(userId);

        /* 更新 */
        try{
            if(name != null){
                updateUser.setName(name);
            }
            if(favorite != null){
                updateUser.setFavoriteId(favorite.getId());
            }
            updateUser.setUpdateDate(System.currentTimeMillis());
        }catch (PersistenceException e){
            logger.catching(Level.ERROR, e);

            if(userFacade.isActive()){
                userFacade.rollback();
            }
            throw e;
        }

        logger.traceExit();
    }

    /**
     * userを論理削除する
     *
     * @param userId                ユーザーID
     * @throws NoResourceException  ユーザーIDが存在しない場合
     */
    public void delete(long userId)throws NoResourceException{
        logger.entry(userId);

        UserEntity user = getById(userId);
        try{
            userFacade.beginTransaction();
            user.setDeleted(UserEntity.DELETED.TRUE.getValue());
            userFacade.commit();
        }catch (PersistenceException e){
            logger.catching(Level.ERROR, e);

            if(userFacade.isActive()){
                userFacade.rollback();
            }
            throw e;
        }

        logger.traceExit();
    }
}