package com.example.web_app.repos;

import com.example.web_app.domain.Order;
import com.example.web_app.domain.Product;
import com.example.web_app.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface OrderRepository extends MongoRepository<Order, Integer> {

    Order findFirstByBuyer(User user);

    Order findFirstByProduct(Product product);

}
