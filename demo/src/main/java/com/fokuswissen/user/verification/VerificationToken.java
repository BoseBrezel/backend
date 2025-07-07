package com.fokuswissen.user.verification;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "verification_tokens")
public class VerificationToken
{

    @Id
    private String id;

    private String token;

    private String userId;

    private LocalDateTime expiryDate;

    public VerificationToken(String token, String userId, LocalDateTime expiryDate)
    {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired()
    {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
