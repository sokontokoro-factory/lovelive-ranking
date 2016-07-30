package net.sokontokoro_factory.lovelive.controller.resource;

import net.sokontokoro_factory.lovelive.controller.dto.ErrorDto;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.service.LoginSession;
import net.sokontokoro_factory.lovelive.service.UserService;
import net.sokontokoro_factory.tweetly_oauth.TweetlyOAuth;
import net.sokontokoro_factory.tweetly_oauth.TweetlyOAuthException;
import net.sokontokoro_factory.tweetly_oauth.dto.AccessToken;
import net.sokontokoro_factory.tweetly_oauth.dto.RequestToken;
import net.sokontokoro_factory.yoshinani.file.config.Config;
import net.sokontokoro_factory.yoshinani.file.config.ConfigLoader;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;


@Path("auth/twitter")
@RequestScoped
public class TwitterAuthResource {
    private static final Config config = ConfigLoader.getProperties();
    private static final String GAME_CLIENT_ORIGIN = config.getString("game.client.origin");

    @Inject
    LoginSession loginSession;

    @Inject
    UserService userService;

    @Context
    UriInfo uriInfo;

    /**
     * ログイン処理を実行する
     *
     * @param redirectPath　ログイン後のリダイレクト先
     * @return
     */
    @Path("login")
    @GET
    public Response login(@QueryParam("redirect_path") String redirectPath){

        TweetlyOAuth tweetlyOAuth = new TweetlyOAuth();

        String callbackUri = uriInfo.getBaseUriBuilder()
                .path(TwitterAuthResource.class)
                .path("/callback")
                .build().toString();

        RequestToken token = null;
        try{
            token = tweetlyOAuth.getRequestToken(callbackUri);
        }catch(TweetlyOAuthException e){
            ErrorDto error = new ErrorDto();
            error.setMessage("faild to access twitter auth providing server");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
        loginSession.setRedirectPathAfterLogging(redirectPath == null? "": redirectPath);
        loginSession.setRequestToken(token);

		// twitter認証画面へリダイレクト
        URI redirect = UriBuilder
                .fromUri("https://api.twitter.com")
                .path("oauth")
                .path("authorize")
                .queryParam("oauth_token", token.getToken()).build();

        return Response.seeOther(redirect).build();
    }

    /**
     * twitterの認証完了後のエンドポイント
     * access_token, access_token_secret, user_id, screen_nameをセッションに登録する
     *
     * @param requestToken
     * @param oauthVerifier
     * @param denied
     * @return
     */
    @Path("callback")
    @GET
    public Response callback(
            @QueryParam("oauth_token")              String requestToken,
            @QueryParam("oauth_verifier")           String oauthVerifier,
            @QueryParam("denied")@DefaultValue("admit")  String denied){

        // リクエストトークン取得済み確認
    	if(loginSession.getRequestToken() == null){
    		return Response.status(Status.UNAUTHORIZED).entity("Unauthorized.Please try to login again, sorry.").build();
    	}

        try {
            // ユーザーが認証許可したか
            if(denied.equals("admit")){
                //認証許可の場合
                TweetlyOAuth tweetly = new TweetlyOAuth();
                AccessToken token = tweetly.getAccessToken(loginSession.getRequestToken(), oauthVerifier);

                loginSession.setAccessToken(token);
                loginSession.setUserId((Long.valueOf(token.getUserId())));
                loginSession.setUserName(token.getScreenName());

                try{
                    userService.getById(loginSession.getUserId());
                }catch(NoResourceException notRegisterd){
                    userService.create(loginSession.getUserId(), loginSession.getUserName());
                }

            }else{
                // 認証不許可の場合
                loginSession.invalidate();
            }
        }catch(TweetlyOAuthException e){
            ErrorDto error = new ErrorDto();
            error.setMessage("faild to access twitter auth providing server");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }

        URI redirect = UriBuilder
                .fromUri(GAME_CLIENT_ORIGIN)
                .path(loginSession.getRedirectPathAfterLogging())
                .build();

        return Response.seeOther(redirect).build();
    }

    /**
     * ログアウトする。
     * ログインセッションを削除し、指定pathへリダイレクトする。
     *
     * @param redirectPath
     * @return
     */
    @Path("logout")
    @GET
    public Response logout(@QueryParam("redirect") String redirectPath){

        loginSession.invalidate();

        URI redirect = UriBuilder
                .fromUri(GAME_CLIENT_ORIGIN)
                .path(loginSession.getRedirectPathAfterLogging())
                .build();

        return Response.seeOther(redirect).build();
    }
}

