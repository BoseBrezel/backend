package com.fokuswissen.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fokuswissen.security.JwtUtil;

@RestController
@RequestMapping("/users")
public class UserController
{
    private final JwtUtil jwtUtil;

    private final UserService userService;

    public UserController(UserService userService, JwtUtil jwtUtil)
    {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
            .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            // Admin bekommt alle User
            List<User> allUsers = userService.getAllUsers();
            return ResponseEntity.ok(allUsers);
        } else {
            // Normaler User bekommt nur sich selbst (per username)
            Optional<User> userOptional = userService.getUserByUsername(userDetails.getUsername());
            if (userOptional.isPresent()) {
                return ResponseEntity.ok(List.of(userOptional.get())); // Als Liste mit einem Element zurückgeben
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
    }

    //Jeder kann sich registrieren
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user)
    {
        User createdUser = userService.register(user.getUsername(), user.getEmailAdress() , user.getPassword());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    //Jeder kann sich registrieren
    @PostMapping("/register/email")
    public ResponseEntity<User> createUserEmail(@RequestBody User user)
    {
        User createdUser = userService.registerEmail(user.getUsername(), user.getEmailAdress() , user.getPassword());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    //Nur Admin oder besitzer kann User ändern
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user, @AuthenticationPrincipal User userDetails) {
        Optional<User> userOptional = userService.getUserById(id);
        if(userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = userOptional.get();
        boolean isAdmin = userDetails.getAuthorities().stream()
            .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));

        if(!isAdmin && !existingUser.getId().equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            User updatedUser = userService.updateUser(
                id,
                user.getUsername(),
                user.getEmailAdress(),
                user.getPassword(),
                user.getPoints(),
                user.getQuizDone()
            );

            // Prüfen, ob Username geändert wurde
            boolean usernameChanged = !existingUser.getUsername().equals(updatedUser.getUsername());

            if (usernameChanged) {
                String newToken = jwtUtil.generateToken(updatedUser.getUsername(), updatedUser.getRoles(), updatedUser.getId());

                return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + newToken)
                    .body(updatedUser);
            }

            return ResponseEntity.ok(updatedUser);
        } catch(RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails)
    {
        Optional<User> userOptional = userService.getUserById(id);
        if(userOptional.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }

        User userToDelete = userOptional.get();

        boolean isAdmin = userDetails.getAuthorities().stream()
            .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !userToDelete.getUsername().equals(userDetails.getUsername()))
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/username")
    public ResponseEntity<Map<String, String>> getUsernameById(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOptional = userService.getUserById(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = userOptional.get();

        Map<String, String> response = new HashMap<>();
        response.put("username", user.getUsername());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails)
    {
        Optional<User> userOptional = userService.getUserById(id);

        if (userOptional.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = userOptional.get();
        boolean isAdmin = userDetails.getAuthorities().stream()
            .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !user.getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(user);
    }
    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token)
    {
        boolean verified = userService.verifyToken(token);
        if(verified)
        {
            return ResponseEntity.ok("Email erfolgreich verifiziert. Du kannst dich jetzt anmelden.");
        }
        else
        {
            return ResponseEntity.badRequest().body("Ungültiger oder abgelaufener Verifikationslink.");
        }
    }
}