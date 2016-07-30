package net.sokontokoro_factory.lovelive.filter;

import net.sokontokoro_factory.lovelive.controller.dto.ErrorDto;
import net.sokontokoro_factory.lovelive.service.LoginSession;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Form認証を要求するREST APIエンドポイントへのリクエストに当てるfilter
 */
@Provider
public class AuthFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    private LoginSession loginSession;

    /**
     * アノテーション定義用内部クラス
     * NGの場合にログインページヘredirctionするかをredirect(引数)で指定する
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface LoginRequired {
        boolean redirect() default false;
    }

    /**
     * "@LoginRequired"がアノテートされたリソース(リソースクラスのメソッド)へのリクエストに対しての検証フィルター。
     * ログイン済みでない場合、401を返却する
     *
     * @param requestContext
     * @throws IOException
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // check whether requested method is annotated, @LoginRequired.
        boolean isAnnotated = resourceInfo.getResourceMethod().getAnnotation(LoginRequired.class) != null;
        if(!isAnnotated){
            return;
        }

        if(!loginSession.isLogin()){
            ErrorDto response = new ErrorDto();
            response.setMessage("unauthorized. please request after logging.");
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity(response)
                            .type(MediaType.APPLICATION_JSON)
                            .build());
        }
    }
}