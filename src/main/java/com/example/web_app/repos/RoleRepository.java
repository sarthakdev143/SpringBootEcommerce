package com.example.web_app.repos;

import com.example.web_app.domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface RoleRepository extends MongoRepository<Role, Integer> {
}
