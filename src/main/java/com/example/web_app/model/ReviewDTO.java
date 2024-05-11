package com.example.web_app.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReviewDTO {

    private Integer reviewId;

    @NotNull
    private Integer stars;

    @NotNull
    private Integer rating;

    @NotNull
    private String comment;

    private LocalDateTime reviewDate;

    @NotNull
    private Integer order;

}
