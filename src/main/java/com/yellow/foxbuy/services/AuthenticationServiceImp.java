package com.yellow.foxbuy.services;

import com.yellow.foxbuy.config.SecurityConfig;
import com.yellow.foxbuy.models.DTOs.AuthResponseDTO;
import com.yellow.foxbuy.models.DTOs.LoginRequest;
import com.yellow.foxbuy.models.DTOs.UserDTO;
import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
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
    private final LogService logService;

    @Autowired
    public AuthenticationServiceImp(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RoleService roleService, EmailService emailService, LogService logService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.roleService = roleService;
        this.emailService = emailService;
        this.logService = logService;
    }

    @Override
    public ResponseEntity<AuthResponseDTO> authenticateUser(LoginRequest loginRequest) {
        AuthResponseDTO response = new AuthResponseDTO();

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (username == null || password == null) {
            response.setMessage("Field username or field password was empty!");
            logService.addLog("POST /login", "ERROR", loginRequest.toString());
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            response.setMessage("Username or password are incorrect.");
            logService.addLog("POST /login", "ERROR", loginRequest.toString());
            return ResponseEntity.badRequest().body(response);
        } else if (user.getVerified() == null || !user.getVerified()) {
            response.setMessage("User is not verified.");
            logService.addLog("POST /login", "ERROR", loginRequest.toString());
            return ResponseEntity.badRequest().body(response);
        } else if (user.getBanned() != null) {
            response.setMessage("User is temporarily banned");
            return ResponseEntity.badRequest().body(response);
        } else if (!passwordEncoder.matches(password, user.getPassword())) {
            response.setMessage("Username or password are incorrect.");
            logService.addLog("POST /login", "ERROR", loginRequest.toString());
            return ResponseEntity.badRequest().body(response);
        }

        String token = jwtUtil.createToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userService.save(user);
        response.setMessage("Login successful.");
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        logService.addLog("POST /login", "INFO", loginRequest.toString());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> verifyJwtToken(String token) {
        Map<String, String> response = new HashMap<>();
        if (jwtUtil.validateToken(token)) {

            String jwtName = jwtUtil.getUsernameFromJWT(token);

            if (userService.findByUsername(jwtName).isPresent()) {

                response.put("id", userService
                        .findByUsername(jwtName)
                        .get()
                        .getId()
                        .toString());

                response.put("username", jwtName);
                logService.addLog("POST /indentity", "INFO", "token = " + token);
                return ResponseEntity.ok().body(response);
            } else {
                response.put("error", "token does not match any user");
                logService.addLog("POST /indentity", "ERROR", "token = " + token);
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("error", "token is not valid");
            logService.addLog("POST /indentity", "ERROR", "token = " + token);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Override
    public Map<String, String> goodRegisterUser(UserDTO userDTO) throws MessagingException {
        Map<String, String> result = new HashMap<>();

        Set<Role> userRole = new HashSet<>();
        if (userService.userRepositoryIsEmpty()) {
            userRole.add(roleService.findRoleByName("ROLE_ADMIN"));
        } else {
            userRole.add(roleService.findRoleByName("ROLE_USER"));
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
    public Map<String, String> badRegisterUser(UserDTO userDTO) {
        Map<String, String> result = new HashMap<>();

        if (userService.existsByUsername(userDTO.getUsername())) {
            result.put("error", "Username already exists.");
        } else if (userService.existsByEmail(userDTO.getEmail())) {
            result.put("error", "Email is already used.");
        }
        logService.addLog("POST /registration", "ERROR", userDTO.toString());
        return result;
    }

    @Override
    public Boolean isRefreshTokenValid(String refreshToken) {
        try {
            return jwtUtil.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    @Override
    public String generateNewJwtToken(User user) {
        return jwtUtil.createToken(user);
    }
}
