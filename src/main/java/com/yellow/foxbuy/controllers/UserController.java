package com.yellow.foxbuy.controllers;

import com.yellow.foxbuy.config.SecurityConfig;
import com.yellow.foxbuy.models.DTOs.LoginRequest;
import com.yellow.foxbuy.models.DTOs.UserDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.services.UserService;
import com.yellow.foxbuy.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/registration")
    public ResponseEntity<?> userRegistration(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult){
        Map<String, String> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                result.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.status(400).body(result);
        }

        if (userService.existsByUsername(userDTO.getUsername()) && userService.existsByEmail(userDTO.getEmail())) {
            result.put("error", "Username and email are already used.");
            return ResponseEntity.status(400).body(result);
        } else if (userService.existsByUsername(userDTO.getUsername())) {
            result.put("error", "Username already exists.");
            return ResponseEntity.status(400).body(result);
        } else if (userService.existsByEmail(userDTO.getEmail())) {
            result.put("error", "Email is already used.");
            return ResponseEntity.status(400).body(result);
        } else {
            User user = new User(userDTO.getUsername(), userDTO.getEmail(), SecurityConfig.passwordEncoder().encode(userDTO.getPassword()));
            userService.save(user);
            System.out.println(jwtUtil.createToken(user));
            result.put("username", user.getUsername());
            result.put("id", String.valueOf(user.getId()));
            return ResponseEntity.status(200).body(result);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> userLoginAndGenerateJWToken(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult){
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Map<String, String> result = new HashMap<>();

        if (username == null || password == null) {
            result.put("error", "Field username or field password was empty!");
            return ResponseEntity.status(400).body(result);

        } else if (userService.existsByUsername(loginRequest.getUsername())) {
            result.put("error", "This user does not exist!");
            return ResponseEntity.status(400).body(result);
            //  } else if (userService.isVerified) Vojtova metoda?



        }
        return null;
    }

}