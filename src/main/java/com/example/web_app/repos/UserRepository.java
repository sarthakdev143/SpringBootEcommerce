package com.example.web_app.repos;

import com.example.web_app.domain.Role;
import com.example.web_app.domain.User;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, Integer> {

    User findFirstByRoleId(Role role);

    List<User> findAllByRoleId(Role role);

    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);

}
