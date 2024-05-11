package com.example.web_app.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document
@Getter
@Setter
public class Grain {

    @Id
    private Integer grainId;

    private String grainName;

    private String description;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;

    private Integer quantity;

    @DocumentReference(lazy = true)
    private User seller;

    @DocumentReference(lazy = true, lookup = "{ 'grain' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Set<Order> grainOrders;

    byte[] thumbnail;

    Set<byte[]> fieldIamgs;
}
