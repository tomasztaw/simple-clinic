package pl.taw.api.controller.rest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Greeting {

    @NotNull(message = "Wiadomość nie może być pusta")
    @NotEmpty(message = "Wiadomość nie może być pusta")
    private String msg;

    @NotNull(message = "Musisz podać nadawcę")
    @NotEmpty(message = "Musisz podać nadawcę")
    private String from;

    @NotNull(message = "Musisz podać odbiorcę")
    @NotEmpty(message = "Musisz podać odbiorcę")
    private String to;

}
