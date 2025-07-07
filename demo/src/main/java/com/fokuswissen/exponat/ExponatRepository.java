package com.fokuswissen.exponat;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExponatRepository extends MongoRepository<Exponat, String>
{
    
}
