package com.example.web_app.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategoryDTO {

    private Integer id;

    @Size(max = 255)
    private String name;

}
