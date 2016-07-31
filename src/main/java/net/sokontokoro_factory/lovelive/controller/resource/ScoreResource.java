package net.sokontokoro_factory.lovelive.controller.resource;

import net.sokontokoro_factory.lovelive.controller.dto.ErrorDto;
import net.sokontokoro_factory.lovelive.controller.dto.ScoreDto;
import net.sokontokoro_factory.lovelive.controller.form.InsertScoreForm;
import net.sokontokoro_factory.lovelive.exception.InvalidArgumentException;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.filter.AuthFilter;
import net.sokontokoro_factory.lovelive.persistence.entity.ScoreEntity;
import net.sokontokoro_factory.lovelive.persistence.master.MasterGame;
import net.sokontokoro_factory.lovelive.service.LogService;
import net.sokontokoro_factory.lovelive.service.LoginSession;
import net.sokontokoro_factory.lovelive.service.ScoreService;
import net.sokontokoro_factory.lovelive.service.UserService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Path("api/scores")
@RequestScoped
public class ScoreResource {

	@Context
    UriInfo uriInfo;

	@Inject
	LoginSession loginSession;

	@Inject
	UserService userService;

	@Inject
	ScoreService scoreService;

	@Inject
	LogService logService;

	/**
	 * ログイン中のUserIDのスコア情報を取得する
	 *
	 * @param gameName
	 * @return
	 * @throws NoResourceException
	 * @throws InvalidArgumentException
     */
	@AuthFilter.LoginRequired
	@Path("{game_name}/me")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getScore(@PathParam(value = "game_name") String gameName)
			throws NoResourceException, InvalidArgumentException {

		// ゲーム名の入力チェック
		MasterGame game = MasterGame.codeOf(gameName);
		if(game == null){
			ErrorDto errorDto = new ErrorDto();
			errorDto.setMessage("正しいゲーム名を指定して下さい");
			return Response.status(Response.Status.NOT_FOUND).entity(errorDto).build();
		}

    	/* エンティティ取得 */
		ScoreEntity scoreEntity = scoreService.getScore (game, loginSession.getUserId());
		long ranking = scoreService.getRankingNumber(game, scoreEntity.getPoint());

    	/* レスポンスデータ作成 */
		ScoreDto score = new ScoreDto();
		score.setGameName(game.getCode());
		score.setUserId(scoreEntity.getUserId());
		score.setUserName(scoreEntity.getUserEntity().getName());
		score.setPoint(scoreEntity.getPoint());
		score.setCount(scoreEntity.getCount());
		score.setRanking(ranking);

    	/* レスポンス */
		return Response.ok().entity(score).build();
	}

    /**
     * スコア登録する
	 *
	 * @param gameName
	 * @param insertScoreForm
	 * @return
	 * @throws InvalidArgumentException
     */
	@AuthFilter.LoginRequired
    @Path("{game_name}/me")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertScore(
    		@PathParam("game_name") String gameName,
									InsertScoreForm insertScoreForm)
									throws InvalidArgumentException {

		// 入力チェック
		if(insertScoreForm.getPoint() == null){
			ErrorDto errorDto = new ErrorDto("点数を入力して下さい");
			return Response.status(Response.Status.BAD_REQUEST).entity(errorDto).build();
		}

		// ゲーム名の入力チェック
		MasterGame game = MasterGame.codeOf(gameName);
		if(game == null){
			ErrorDto errorDto = new ErrorDto();
			errorDto.setMessage("正しいゲーム名を指定して下さい");
			return Response.status(Response.Status.NOT_FOUND).entity(errorDto).build();
		}

    	/* DB書き込み */
		scoreService.insertScore(
				game,
				loginSession.getUserId(),
				insertScoreForm.getPoint());

		/* ロギング */
		logService.addGameLog(
				game,
				loginSession.getUserId(),
				insertScoreForm.getPoint());

    	/* レスポンス */
		URI uri = uriInfo.getBaseUriBuilder()
				.path(ScoreResource.class)
				.path(game.getCode())
				.path("me")
				.build();

    	return Response.created(uri).build();
    }

    /**
     * 指定順位以下のランキングスコアリストを取得する
	 *
	 * @param gameName
	 * @param offset
	 * @return
     */
    @Path("/{game_name}/ranking")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRankingData(
			@PathParam(value = "game_name")
					String gameName,
			@QueryParam(value = "offset")
			@DefaultValue("1")
					Integer offset){

		// ゲーム名の入力チェック
		MasterGame game = MasterGame.codeOf(gameName);
		if(game == null){
			ErrorDto errorDto = new ErrorDto();
			errorDto.setMessage("正しいゲーム名を指定して下さい");
			return Response.status(Response.Status.NOT_FOUND).entity(errorDto).build();
		}

		// offset値の入力チェック
		if(offset <= 0){
			ErrorDto error = new ErrorDto("ランキングのoffset値は1以上を指定して下さい。");
			return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
		}

    	/* エンティティ取得 */
    	List<ScoreEntity> scoreEntities = scoreService.getTops(game, offset);

		List<ScoreDto> scores = new ArrayList();
		long ranking = 0;
		int upperUserPoint = Integer.MAX_VALUE;
		for(ScoreEntity scoreEntity: scoreEntities){
			int targetUserPoint = scoreEntity.getPoint();
			if(targetUserPoint < upperUserPoint){
				ranking ++;
			}

			ScoreDto score = new ScoreDto();
			score.setUserId(scoreEntity.getUserId());
			score.setPoint(targetUserPoint);
			score.setUserName(scoreEntity.getUserEntity().getName());
			score.setHighScoreDate(scoreEntity.getUpdateDate());
			score.setRanking(ranking);

			scores.add(score);

			upperUserPoint = targetUserPoint;
		}

    	/* レスポンスデータ作成 */
		Map<String, Object> response = new HashMap();
		response.put("game_name", gameName);
		response.put("ranking_table", scores);

    	/* レスポンス */
    	return Response.ok().entity(response).build();

    }
}