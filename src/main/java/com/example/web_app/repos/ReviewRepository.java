package com.example.web_app.repos;

import com.example.web_app.domain.Order;
import com.example.web_app.domain.Review;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ReviewRepository extends MongoRepository<Review, Integer> {

    Review findFirstByOrder(Order order);

}
