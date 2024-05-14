package com.example.web_app.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;


@Document
@Getter
@Setter
public class User {

    @Id
    private Integer userId;

    @NotNull
    @Size(max = 50)
    private String userName;

    @NotNull
    @Size(max = 100)
    private String email;

    @NotNull
    @Size(max = 200)
    private String password;

    private LocalDateTime createdAt;

    @DocumentReference(lazy = true, lookup = "{ 'user' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Set<Product> products;

    @DocumentReference(lazy = true, lookup = "{ 'buyer' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Set<Order> buyerOrders;

    @DocumentReference(lazy = true, lookup = "{ 'sender' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Set<Message> senderMessages;

    @DocumentReference(lazy = true, lookup = "{ 'receiver' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Set<Message> receiverMessages;

    @DocumentReference(lazy = true)
    private Set<Role> roleId;

}
