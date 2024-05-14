package com.example.web_app.domain;

import jakarta.validation.constraints.Size;
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
public class Category {

    @Id
    private Integer id;

    @Size(max = 255)
    private String name;

    @DocumentReference(lazy = true, lookup = "{ 'category' : ?#{#self._id} }")
    @ReadOnlyProperty
    private Set<Product> products;

}
