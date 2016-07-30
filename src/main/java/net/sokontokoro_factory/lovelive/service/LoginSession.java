package net.sokontokoro_factory.lovelive.service;

import lombok.Getter;
import lombok.Setter;
import net.sokontokoro_factory.tweetly_oauth.dto.AccessToken;
import net.sokontokoro_factory.tweetly_oauth.dto.RequestToken;

import javax.enterprise.context.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.Serializable;

@SessionScoped
public class LoginSession implements Serializable{

    @Context
    @Getter
    HttpServletRequest request;


    /* field */
    @Getter
    @Setter
    RequestToken requestToken;

    @Getter
    @Setter
    AccessToken accessToken;

    @Getter
    @Setter
    String redirectPathAfterLogging;

    @Getter
    @Setter
    Long userId;

    @Getter
    @Setter
    String userName;

    @Getter
    @Setter
    String skntkrToken;

    public boolean isLogin(){
        return accessToken != null? true: false;
    }

    public void invalidate(){
        // 変数初期化
        requestToken = null;
        accessToken = null;
        redirectPathAfterLogging = null;

        // セッションID再作成
        request.getSession(false).invalidate();
    }
}
