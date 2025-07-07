package com.fokuswissen.user;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.fokuswissen.user.mail.MailService;
import com.fokuswissen.user.verification.VerificationToken;
import com.fokuswissen.user.verification.VerificationTokenRepository;

import io.jsonwebtoken.lang.Collections;

@Service
public class UserService implements UserDetailsService
{
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final MailService mailService;

    public UserService(UserRepository userRepository, VerificationTokenRepository tokenRepository, MailService mailService )
    {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailService = mailService;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Optional<User> getUserByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    public boolean verifyToken(String token)
    {
            Optional<VerificationToken> verificationTokenOpt = tokenRepository.findByToken(token);

            if(verificationTokenOpt.isEmpty())
            {
                return false;
            }

            VerificationToken verificationToken = verificationTokenOpt.get();

            if(verificationToken.isExpired())
            {
                return false;
            }

            Optional<User> userOpt = userRepository.findById(verificationToken.getUserId());
            if(userOpt.isEmpty())
            {
                return false;
            }

            User user = userOpt.get();
            user.setEnabled(true); // User aktivieren
            userRepository.save(user);

            tokenRepository.delete(verificationToken); // Token löschen

            return true;
        }

    //Registrierung nur mit nicht vergebenem Username und nicht vergebener Email
    public User register(String username, String email, String password) {
        if(userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username bereits vergeben");
        }
        if(userRepository.existsByEmailAdress(email)) {
            throw new IllegalArgumentException("Email-Adresse bereits vergeben");
        }
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_USER);
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = User.builder()
            .username(username)
            .emailAdress(email)
            .password(hash)
            .enabled(true)
            .points("0") // default Punkte
            .quizDone(List.of()) // leere Liste
            .roles(roles)
            .build();
        System.out.println("user " + user);
        User savedUser = userRepository.save(user);

        return savedUser;
    }

    public User registerEmail(String username,String email, String password)
    {
        if(userRepository.existsByUsername(username))
        {
            throw new IllegalArgumentException("Username bereits vergeben");
        }
        if(userRepository.existsByEmailAdress(email))
        {
            throw new IllegalArgumentException("Email-Adresse bereits vergeben");
        }
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_USER);

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = User.builder()
            .username(username)
            .emailAdress(email)
            .password(hash)
            .points("0")
            .quizDone(List.of())
            .roles(roles)
            .build();
        User savedUser = userRepository.save(user);

        // Token erzeugen und speichern
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedUser.getId(), LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        // E-Mail mit Token senden
        mailService.sendVerificationEmail(email, token);
        return savedUser;
    }

    public Optional<User> authenticate(String username, String password)
    {
        return userRepository.findByUsername(username)
                            .filter(user -> user.isEnabled())  // Nur aktivierte User
                            .filter(user -> BCrypt.checkpw(password, user.getPassword()));
    }

    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id)
    {
        return userRepository.findById(id);
    }

    public void deleteUser(String id)
    {
        userRepository.deleteById(id);
    }
    
    public User updateUser(String id, String username, String email, String password, String points, List<String> quizDone) {
        return userRepository.findById(id).map(user -> {
            if (username != null && !username.isEmpty() && !username.equals(user.getUsername())) {
                if (userRepository.existsByUsername(username)) {
                    throw new IllegalArgumentException("Benutzername ist bereits vergeben");
                }
                user.setUsername(username);
            }

            if (email != null && !email.isEmpty() && !email.equals(user.getEmailAdress())) {
                if (userRepository.existsByEmailAdress(email)) {
                    throw new IllegalArgumentException("E-Mail-Adresse ist bereits vergeben");
                }
                user.setEmailAdress(email);
            }

            if (password != null && !password.isEmpty()) {
                String hash = BCrypt.hashpw(password, BCrypt.gensalt());
                user.setPassword(hash);
            }

            if(points != null) {
                user.setPoints(points);
            }

            if(quizDone != null) {
                user.setQuizDone(quizDone);
            }

            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
    