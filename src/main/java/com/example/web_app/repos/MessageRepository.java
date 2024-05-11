package com.example.web_app.repos;

import com.example.web_app.domain.Message;
import com.example.web_app.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MessageRepository extends MongoRepository<Message, Integer> {

    Message findFirstBySender(User user);

    Message findFirstByReceiver(User user);

}
