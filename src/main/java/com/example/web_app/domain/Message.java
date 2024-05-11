package com.example.web_app.domain;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;


@Document
@Getter
@Setter
public class Message {

    @Id
    private Integer messageId;

    @NotNull
    private String message;

    private LocalDateTime sendDate;

    @DocumentReference(lazy = true)
    @NotNull
    private User sender;

    @DocumentReference(lazy = true)
    @NotNull
    private User receiver;

}
