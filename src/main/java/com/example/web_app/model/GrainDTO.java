package com.example.web_app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GrainDTO {

    private Integer grainId;

    @NotNull
    @Size(max = 50)
    @GrainGrainNameUnique
    private String grainName;

    @NotNull
    private String description;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;

    @NotNull
    private Integer quantity;

    @NotNull
    private Integer seller;

}
