package com.example.web_app.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Integer userId;
    private String userName;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private List<Integer> roleId;
    private Integer roleIdCount;

}
