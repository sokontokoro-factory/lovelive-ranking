package net.sokontokoro_factory.lovelive.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import net.sokontokoro_factory.lovelive.type.GameType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;


@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement
public class ScoreDto {
	
	@XmlElement(name = "game")
	@Getter
	@Setter
	private GameType game;
	
	@XmlElement(name = "user_id")
	@Getter
	@Setter
	private Long userId;

	@XmlElement(name = "user_name")
	@Getter
	@Setter
	private String userName;
	
	@XmlElement(name = "point")
	@Getter
	@Setter
	private Integer point;
	
	@XmlElement(name = "high_score_date")
	@Getter
	@Setter
	private Long highScoreDate;

	@XmlElement(name = "count")
	@Getter
	@Setter
	private Integer count;
	
	@XmlElement(name = "ranking")
	@Getter
	@Setter
	private Long ranking;
}