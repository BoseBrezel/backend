package com.fokuswissen.user.verification;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String>
{
    Optional<VerificationToken> findByToken(String token);
}
