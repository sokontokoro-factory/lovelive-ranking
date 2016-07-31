package net.sokontokoro_factory.lovelive.controller.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InsertScoreForm {
    @XmlElement(name = "point")
    @Getter
    @Setter
    private Integer point;
}
