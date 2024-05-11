package com.example.web_app.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageDTO {

    private Integer messageId;

    @NotNull
    private String message;

    private LocalDateTime sendDate;

    @NotNull
    private Integer sender;

    @NotNull
    private Integer receiver;

}
