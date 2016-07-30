package net.sokontokoro_factory.lovelive.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.List;

@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
	@XmlElement(name = "user_id")
	@Getter
	@Setter
	private Long id;
	
	@XmlElement(name = "user_name")
	@Getter
	@Setter
	private String name;
	
	@XmlElement(name = "admin")
	@Getter
	@Setter
	private Boolean admin;
	
	@XmlElement(name = "deleted")
	@Getter
	@Setter
	private Boolean deleted;
	
	@XmlElement(name = "create_date")
	@Getter
	@Setter
	private Timestamp createDate;
	
	@XmlElement(name = "update_date")
	@Getter
	@Setter
	private Timestamp updateDate;
	
	@XmlElement(name = "icon_url")
	@Getter
	@Setter
	private String iconURL;
	
	@XmlElement(name = "scores")
	@Getter
	@Setter
	private List<ScoreDto> scores;
}