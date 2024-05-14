package com.example.web_app.repos;

import com.example.web_app.domain.Category;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CategoryRepository extends MongoRepository<Category, Integer> {
}
