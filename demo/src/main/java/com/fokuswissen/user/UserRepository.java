package com.fokuswissen.user;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "user", path = "user")
public interface UserRepository extends MongoRepository<User, String>
{
  public Optional<User> findByUsername(String username);
  public Optional<User> findByemailAdress(String emailAdress);
  Boolean existsByUsername(String username);
  Boolean existsByEmailAdress(String emailAdress);
}