package com.example.web_app.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class Order {

    @Id
    private Integer orderId;

    private Integer quantity;

    @Digits(integer = 12, fraction = 2)
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalPrice;

    private LocalDateTime orderDate;

    @DocumentReference(lazy = true)
    @NotNull
    private User buyer;

    @DocumentReference(lazy = true)
    @NotNull
    private Product product;

    @DocumentReference(lazy = true, lookup = "{ 'order' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Set<Review> orderReviews;

}
