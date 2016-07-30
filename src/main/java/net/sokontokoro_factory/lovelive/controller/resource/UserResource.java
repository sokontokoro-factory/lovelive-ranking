package net.sokontokoro_factory.lovelive.controller.resource;

import net.sokontokoro_factory.lovelive.controller.dto.UserDto;
import net.sokontokoro_factory.lovelive.controller.form.UpdateUserForm;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
//import net.sokontokoro_factory.lovelive.filter.AuthFilter;
import net.sokontokoro_factory.lovelive.filter.AuthFilter;
import net.sokontokoro_factory.lovelive.persistence.entity.UserEntity;
//import net.sokontokoro_factory.lovelive.service.LoginSession;
import net.sokontokoro_factory.lovelive.service.LoginSession;
import net.sokontokoro_factory.lovelive.service.UserService;
import net.sokontokoro_factory.tweetly_oauth.TweetlyOAuthException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("api/users")
@RequestScoped
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    LoginSession loginSession;

    /**
     * ログインアカウントの情報を返す。
     * user_id, user_nameはsokontokoro-server, iconURLはtwitter server RestAPIへ問い合わせる。
     *
     * @return　{user_id:***, user_name: ***, iconURL: ***}
     * @throws NoResourceException
     * @throws TweetlyOAuthException
     */
    @AuthFilter.LoginRequired
    @Path("me")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMyInfo() throws NoResourceException{

        /* execute */
        UserEntity user = userService.getById(loginSession.getUserId());

		/* twitterサーバーへの問い合わせ */
        String imageUrl = null;
        try{
            imageUrl = userService.getProfileImageUrl(user.getId(), loginSession.getAccessToken());
        }catch (TweetlyOAuthException ignore){}

    	/* レスポンス */
        UserDto response = new UserDto();
        response.setId(user.getId());
        response.setName(user.getName());
        if(user != null){
            response.setIconURL(imageUrl);
        }
        return Response.ok().entity(response).build();
    }

    /**
     * ユーザー情報をformにしたがって更新する
     * session取得時にユーザー登録、有効化が実施されている前提
     *
     * @param updateUserForm
     * @return
     * @throws NoResourceException
     */
    @AuthFilter.LoginRequired
    @Path("me")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@Valid UpdateUserForm updateUserForm) throws NoResourceException{

    	/* execute */
        userService.update(
                loginSession.getUserId(),
                updateUserForm.getUserName(),
                updateUserForm.getFavoriteId());

        return Response.ok().build();
    }

    /**
     * ユーザーを削除する
     *
     * @return
     * @throws NoResourceException
     */
    @AuthFilter.LoginRequired
    @Path("me")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMyInfo() throws NoResourceException{

    	/* execute */
        userService.delete(loginSession.getUserId());

    	/* レスポンス */
        return Response.noContent().build();
    }
}