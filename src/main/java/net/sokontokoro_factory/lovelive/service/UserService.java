package net.sokontokoro_factory.lovelive.service;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.persistence.entity.UserEntity;
import net.sokontokoro_factory.lovelive.persistence.facade.UserFacade;
import net.sokontokoro_factory.lovelive.type.FavoriteType;
import net.sokontokoro_factory.yoshinani.file.config.Config;
import net.sokontokoro_factory.yoshinani.file.config.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RequestScoped
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);

    private static final Config config = ConfigLoader.getProperties();
    private static final String TWITTER_APIKEY = config.getString("twitter.apikey");
    private static final String TWITTER_SECRET = config.getString("twitter.secret");

    @Inject
    UserFacade userFacade;

    public String getProfileImageUrl(long userId, OAuth1AccessToken accessToken) throws InterruptedException, ExecutionException, IOException {
        logger.entry(userId, accessToken);

        /* twitterサーバーへの問い合わせ */
        final OAuth10aService service = new ServiceBuilder(TWITTER_APIKEY)
                .apiSecret(TWITTER_SECRET)
                .build(TwitterApi.instance());
        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/users/show.json?user_id=" + userId);

        service.signRequest(accessToken, request);
        String body = service.execute(request).getBody();
        JSONObject usersShowJson = new JSONObject(body);

        String profileImageUrl = usersShowJson.get("profile_image_url").toString();
        return logger.traceExit(profileImageUrl);
    }

    /**
     * 論理削除されていないユーザーのID検索を行う
     *
     * @param userId
     * @return
     * @throws NoResourceException 存在しない、または論理削除済みの場合
     */
    public UserEntity getById(long userId) throws NoResourceException {
        logger.entry(userId);

        UserEntity user = userFacade.findById(userId);
        if (user == null) {
            throw new NoResourceException("指定されたIDは未登録です。");
        } else if (user.isDeleted()) {
            throw new NoResourceException("削除済みのユーザーです。");
        } else {
            return logger.traceExit(user);
        }
    }

    /**
     * userを新規作成する。
     * 論理削除済みのユーザーの場合、deleteフラグの削除とuserNameの更新を行う
     *
     * @param userId
     * @param name
     */
    @Transactional
    public void create(long userId, String name) {
        logger.entry(userId, name);

        UserEntity user = userFacade.findById(userId);

        if (user != null) {
            /* 既存レコードのため、論理削除を外す */
            user.setName(name);
            user.setDeleted(false);
        } else {
            /* レコード新規作成 */
            UserEntity createUser = new UserEntity();
            createUser.setId(userId);
            createUser.setName(name);
            createUser.setCreateDate(System.currentTimeMillis());
            createUser.setDeleted(false);
            createUser.setAdmin(false);
            userFacade.create(createUser);
        }
        logger.traceExit();
    }

    /**
     * user情報を更新する。nullまたは空文字の項目は更新しない
     * 更新対象：ユーザー名、論理削除フラグ
     *
     * @param userId   ユーザーID
     * @param name
     * @param favorite
     * @throws NoResourceException ユーザーIDが存在しない場合
     */
    @Transactional
    public void update(
            long userId,
            String name,
            FavoriteType favorite) throws NoResourceException {

        logger.entry(userId, name, favorite);

        /* 更新対象のuser objectを取得 */
        UserEntity updateUser = getById(userId);

        /* 更新 */
        if (name != null) {
            updateUser.setName(name);
        }
        if (favorite != null) {
            updateUser.setFavorite(favorite);
        }
        updateUser.setUpdateDate(System.currentTimeMillis());

        logger.traceExit();
    }

    /**
     * userを論理削除する
     *
     * @param userId ユーザーID
     * @throws NoResourceException ユーザーIDが存在しない場合
     */
    @Transactional
    public void delete(long userId) throws NoResourceException {
        logger.entry(userId);

        UserEntity user = getById(userId);
        user.setDeleted(true);

        logger.traceExit();
    }
}