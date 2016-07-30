package net.sokontokoro_factory.lovelive.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAX-RSによるErrorレスポンスを表現するJavaBean
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
@XmlRootElement
public class ErrorDto {
    @XmlElement(name = "target")
    @Getter
    @Setter
    private String target;

    @XmlElement(name = "message")
    @Getter
    @Setter
    private String message;

    public ErrorDto(){}

    public ErrorDto(String message){
        this.message = message;
    }
}