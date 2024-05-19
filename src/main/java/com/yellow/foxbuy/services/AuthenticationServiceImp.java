package com.yellow.foxbuy.services;

import com.yellow.foxbuy.config.SecurityConfig;
import com.yellow.foxbuy.models.DTOs.LoginRequest;
import com.yellow.foxbuy.models.DTOs.UserDTO;
import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Map<String, String> registerUserSuccessful(UserDTO userDTO) throws MessagingException {
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
    public Map<String, String> registerUserFailed(UserDTO userDTO) {
        Map<String, String> result = new HashMap<>();

        if (userService.existsByUsername(userDTO.getUsername())) {
            result.put("error", "Username already exists.");
        } else if (userService.existsByEmail(userDTO.getEmail())) {
            result.put("error", "Email is already used.");
        }
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

    @Override
    public Map<String, String> loginUserFailed(LoginRequest loginRequest) {
        Map<String, String> result = new HashMap<>();

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            result.put("message", "Username or password are incorrect.");
        } else if (user.getVerified() == null || !user.getVerified()) {
            result.put("message", "User is not verified.");
        } else if (user.getBanned() != null) {
            result.put("message", "User is temporarily banned");
        } else if (!passwordEncoder.matches(password, user.getPassword())) {
            result.put("message", "Username or password are incorrect.");
        }
        return result;
    }

    @Override
    public Map<String, String> loginUserSuccessful(LoginRequest loginRequest) {
        Map<String, String> result = new HashMap<>();
        String username = loginRequest.getUsername();
        User user = userService.findByUsername(username).orElse(null);

        String token = jwtUtil.createToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userService.save(user);
        result.put("message", "Login successful.");
        result.put("token", token);
        result.put("refreshToken", refreshToken);
        return result;
    }

    @Override
    public Map<String, String> verifyJwtTokenFailed(String token) {
        Map<String, String> response = new HashMap<>();
        if (!jwtUtil.validateToken(token)){
            response.put("error", "token is not valid");
            return response;
        }
        String jwtName = jwtUtil.getUsernameFromJWT(token);
        if (userService.findByUsername(jwtName).isEmpty()) {
            response.put("error", "token does not match any user");
            return response;
        }
        return response;
    }

    @Override
    public Map<String, String> verifyJwtTokenSuccessful(String token) {
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
            }
        }
        return response;
    }
}

