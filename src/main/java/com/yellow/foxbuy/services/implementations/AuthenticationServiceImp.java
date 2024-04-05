package com.yellow.foxbuy.services.implementations;

import com.yellow.foxbuy.config.SecurityConfig;
import com.yellow.foxbuy.models.DTOs.AuthResponseDTO;
import com.yellow.foxbuy.models.DTOs.LoginRequest;
import com.yellow.foxbuy.models.DTOs.UserDTO;
import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.interfaces.AuthenticationService;
import com.yellow.foxbuy.services.interfaces.EmailService;
import com.yellow.foxbuy.services.interfaces.RoleService;
import com.yellow.foxbuy.services.interfaces.UserService;
import com.yellow.foxbuy.utils.JwtUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AuthenticationServiceImp implements AuthenticationService {


    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;
    private final RoleService roleService;
    private final EmailService emailService;
    @Autowired
    public AuthenticationServiceImp(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RoleService roleService, EmailService emailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.roleService = roleService;
        this.emailService = emailService;
    }

    @Override
    public ResponseEntity<AuthResponseDTO> authenticateUser(LoginRequest loginRequest) {
        AuthResponseDTO response = new AuthResponseDTO();

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null || password == null) {
            response.setMessage("Field username or field password was empty!");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            response.setMessage("Username or password are incorrect.");
            return ResponseEntity.badRequest().body(response);
        } else if (!user.getVerified()) {
            response.setMessage("User is not verified.");
            return ResponseEntity.badRequest().body(response);
        } else if (!passwordEncoder.matches(password, user.getPassword())) {
            response.setMessage("Username or password are incorrect.");
            return ResponseEntity.badRequest().body(response);
        }

        String token = jwtUtil.createToken(user);
        response.setMessage("Login successful.");
        response.setToken(token);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?>  verifyJwtToken(String token) {
        Map<String, String> response = new HashMap<>();
        if (jwtUtil.validateToken(token)){

            String jwtName = jwtUtil.getUsernameFromJWT(token);

            if (userService.findByUsername(jwtName).isPresent()){

                response.put("id", userService
                        .findByUsername(jwtName)
                        .get()
                        .getId()
                        .toString());

                response.put("username", jwtName);
                return ResponseEntity.ok().body(response);
            } else  {
                response.put("error", "token does not match any user");
                return ResponseEntity.badRequest().body(response);
            }
        } else{
            response.put("error", "token is not valid");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Override
    public Map<String, String> goodRegisterUser(UserDTO userDTO) throws MessagingException {
        Map<String, String> result = new HashMap<>();

        Set<Role> userRole = new HashSet<>();
        System.out.println(userService.userRepositoryIsEmpty());
        if (userService.userRepositoryIsEmpty()) {
            userRole.add(roleService.getReferenceById(3L));
        } else {
            userRole.add(roleService.getReferenceById(1L));
        }

        User user = new User(userDTO.getUsername(),
                userDTO.getEmail(),
                SecurityConfig.passwordEncoder().encode(userDTO.getPassword()), userRole);

        String emailVerification = System.getenv("EMAIL_VERIFICATION");

        if (emailVerification == null || emailVerification.equals("on")) {
            userService.save(user);
            emailService.sendVerificationEmail(user);
        } else {
            user.setVerified(true);
            userService.save(user);
        }

        result.put("username", user.getUsername());
        result.put("id", String.valueOf(user.getId()));
        return result;
    }

    @Override
    public Map<String, String> badRegisterUser(UserDTO userDTO){
        Map<String, String> result = new HashMap<>();

        if (userService.existsByUsername(userDTO.getUsername())) {
            result.put("error", "Username already exists.");
        } else if (userService.existsByEmail(userDTO.getEmail())) {
            result.put("error", "Email is already used.");
        }
        return result;
    }
}
