package com.example.web_app.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
public class RoleDTO {

    public Integer id;

    @NotNull
    @Size(max = 255)
    public String name;

}
