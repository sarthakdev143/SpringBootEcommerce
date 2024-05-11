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
public class Review {

    @Id
    private Integer reviewId;

    @NotNull
    private Integer stars;

    @NotNull
    private Integer rating;

    @NotNull
    private String comment;

    private LocalDateTime reviewDate;

    @DocumentReference(lazy = true)
    @NotNull
    private Order order;

}
