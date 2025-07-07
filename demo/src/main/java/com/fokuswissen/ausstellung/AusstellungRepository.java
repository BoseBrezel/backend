package com.fokuswissen.ausstellung;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AusstellungRepository extends MongoRepository<Ausstellung, String> {
}
