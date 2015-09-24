package net.sokontokoro_factory.api.game.score;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;


@Path("")
public class ScoreRestController {

	final String ORIGIN = "http://diary.sokontokoro-factory.net";
	final static int NUMBER_OF_TOP = 10;


    // ゲームの指定なしでスコアは取得できない
    @Path("")
    @GET
    @Produces("text/plain;charset=UTF-8")
    public Response getScoresButError() throws Exception{
    	return Response
    			.status(Status.BAD_REQUEST)
    			.entity("PLEASE SELECT GAME")
				.header("Access-Control-Allow-Credentials", true)
				.build();
    }

    /**
     * スコア登録
     * @param game_name
     * @param request
     * @param score
     * @return
     */
    @Path("/{game_name}")
    @POST
    @Consumes("application/json;charset=UTF-8")
    public Response insertScore(
    						@PathParam(value = "game_name") String game_name, 
    						@Context HttpServletRequest request, 
    						ScoreResource score){

    	HttpSession session = request.getSession(false);
    	if(session == null){
    		return Response.status(Status.UNAUTHORIZED).entity("認証されていません。").build();
    	}

		final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setMustRevalidate(true);
        cacheControl.setNoStore(true);
    	
    	int point = score.point;
    	String category = score.category;
    	int user_id = Integer.parseInt((String) session.getAttribute("user_id"));

    	// TODO user_idがセッションから撮れなかった場合の値の確認
    	if(user_id == 0){
        	return Response
        			.status(Status.BAD_REQUEST).entity("00error")
					.header("Access-Control-Allow-Credentials", true)
					.cacheControl(cacheControl)
					.build();
    	}
    	
    	
    	try {
			ScoreService.insertScore(game_name, category, user_id, point);
		} catch (SQLException e) {
			return Response
					.status(Status.BAD_REQUEST).entity("SQLみす")
					.header("Access-Control-Allow-Credentials", true)
					.build();
		}catch (Exception e) {
			return Response
					.status(Status.BAD_REQUEST).entity("登録みす")
					.header("Access-Control-Allow-Credentials", true)
					.build();
		}
    	
    	return Response
    			.ok().entity("スコア登録が完了しました。(User_id=" + (String)session.getAttribute("user_id") + ")")
				.header("Access-Control-Allow-Credentials", true)
				.build();
    }

    /**
     * 上位"NUMBER_OF_TOP"のランキング情報を取得
     * @param game_name
     * @param request
     * @return
     * @throws Exception 
     */
    @Path("/{game_name}/ranking")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getRankingInformation(
			@PathParam(value = "game_name") String game_name, 
			@Context HttpServletRequest request) throws Exception{
    	
		HttpSession session = request.getSession(false);
    	if(session == null){
    		return Response.status(Status.UNAUTHORIZED).entity("認証されていません。").build();
    	}
    	JSONArray scores;
    	try {
        	scores =  ScoreService.getHigher(game_name, NUMBER_OF_TOP);
		} catch (SQLException e) {
			return Response
        			.status(Status.BAD_REQUEST)
        			.entity("SQLえらー")
        			.build();
		}catch (Exception e) {
			return Response
        			.status(Status.INTERNAL_SERVER_ERROR)
        			.entity("登録えらー")
        			.build();
		}
    	return Response.ok().entity(scores.toString()).build();

    }
    
    
    /**
     * 
     * @param game_name
     * @param request
     * @return
     */
    @Path("/{game_name}")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getScores(
    						@PathParam(value = "game_name") String game_name, 
    						@Context HttpServletRequest request){

    	
    	JSONArray result = null;
    	try {
			result = ScoreService.getScores(game_name);
		} catch (SQLException e) {
			return Response
        			.status(Status.BAD_REQUEST)
        			.entity("SQLえらー")
        			.build();
		}catch (Exception e) {
			return Response
        			.status(Status.INTERNAL_SERVER_ERROR)
        			.entity("登録えらー")
        			.build();
		}
    	
    	return Response.ok().entity(result.toString()).build();
    }
    
    /**
     * 
     * @param game_name
     * @param request
     * @return
     */
    @Path("/{game_name}/me")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getMyScore(
    						@PathParam(value = "game_name") String game_name, 
    						@Context HttpServletRequest request){

		HttpSession session = request.getSession(false);
    	if(session == null){
    		return Response.status(Status.UNAUTHORIZED).entity("認証されていません。").build();
    	}
    	int user_id = Integer.parseInt((String) session.getAttribute("user_id"));
    	JSONArray result = null;
    	
    	try {
			result = ScoreService.getMyScore(game_name, user_id);
		} catch (SQLException e) {
			return Response
        			.status(Status.BAD_REQUEST)
        			.entity("SQLえらー")
        			.build();
		}catch (Exception e) {
			return Response
        			.status(Status.INTERNAL_SERVER_ERROR)
        			.entity("登録えらー")
        			.build();
		}
    	
    	return Response.ok().entity(result.toString()).build();
    }
}

