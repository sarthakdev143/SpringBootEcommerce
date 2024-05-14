package com.example.web_app.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document
@Getter
@Setter
public class Product {

    @Id
    private Integer productId;

    @Size(max = 50)
    private String name;

    private String description;

    @Digits(integer = 12, fraction = 2)
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;

    private Integer quantity;

    @DocumentReference(lazy = true)
    @NotNull
    private User user;

    @DocumentReference(lazy = true, lookup = "{ 'product' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Set<Order> orders;

    @DocumentReference(lazy = true)
    private Category category;

    private byte[] thumbnail;

    private Set<byte[]> fieldIamgs;

}
