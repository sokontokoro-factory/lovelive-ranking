package net.sokontokoro_factory.lovelive.controller.form;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by TATSUYA-PC4 on 2016/06/22.
 */
@XmlRootElement
public class InsertScoreForm {
    @XmlElement(name = "game_name")
    @Getter
    @Setter
    private String gameName;

    @XmlElement(name = "user_id")
    @Getter
    @Setter
    private Integer userId;

    @XmlElement(name = "point")
    @Getter
    @Setter
    private Integer point;

    @XmlElement(name = "skntkt_token")
    @Getter
    @Setter
    private String skntktToken;
}
