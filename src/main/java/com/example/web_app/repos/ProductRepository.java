package com.example.web_app.repos;

import com.example.web_app.domain.Category;
import com.example.web_app.domain.Product;
import com.example.web_app.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;;

public interface ProductRepository extends MongoRepository<Product, Integer> {

    Product findFirstByUser(User user);

    Product findFirstByCategory(Category category);

    List<Product> findByUser(User user);

}
