package com.example.web_app.repos;

import com.example.web_app.domain.Grain;
import com.example.web_app.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;



public interface GrainRepository extends MongoRepository<Grain, Integer> {

    Grain findFirstBySeller(User user);

    boolean existsByGrainNameIgnoreCase(String grainName);

    List<Grain> findBySeller(User seller);
}
