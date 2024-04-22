package com.yellow.foxbuy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellow.foxbuy.config.SecurityConfig;
import com.yellow.foxbuy.models.DTOs.*;
import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.RoleRepository;
import com.yellow.foxbuy.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class StripeControllerTest {
    private final MockMvc mockMvc;
    private final UserRepository userRepository;
    private ObjectMapper objectMapper;
    private final RoleRepository roleRepository;

    @Autowired
    public StripeControllerTest(MockMvc mockMvc,
                                UserRepository userRepository,
                                RoleRepository roleRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;

    }

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void processVipPayment() throws Exception {
        Role roleUser = roleRepository.save(new Role("ROLE_USER"));

        User user1 = new User("JohnUSER",
                "email@email.com",
                SecurityConfig.passwordEncoder().encode("Password123%"),
                new HashSet<>(Collections.singletonList(roleUser)));
        user1.setVerified(true);
        userRepository.save(user1);

        LoginRequest loginRequest = new LoginRequest("JohnUSER", "Password123%");

        List<User> users = userRepository.findAll();
        long initialCount = users.stream().filter(ads -> ads.getAddress() != null).count();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AuthResponseDTO response = objectMapper.readValue(content, AuthResponseDTO.class);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFullName("Test User");
        customerDTO.setPaymentMethod("pm_card_visa");
        customerDTO.setAddress("Holubi dum 25, Horni dolni  189 56");

        mockMvc.perform(MockMvcRequestBuilders.post("/vip")
                        .header("Authorization", "Bearer " + response.getToken())
                        .content(objectMapper.writeValueAsString(customerDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.message", is("Payment successful. You are now a VIP member!")));

        List<User> users2 = userRepository.findAll();
        long latestCount = users2.stream().filter(ads -> ads.getAddress() != null).count();
        assertEquals(initialCount + 1, latestCount);
    }

    @Test
    public void processVipPaymentFAILED() throws Exception {
        Role roleAdmin = roleRepository.save(new Role("ROLE_ADMIN"));

        User user1 = new User("JohnADMIN",
                "email@email.com",
                SecurityConfig.passwordEncoder().encode("Password123%"),
                new HashSet<>(Collections.singletonList(roleAdmin)));
        user1.setVerified(true);
        userRepository.save(user1);

        LoginRequest loginRequest = new LoginRequest("JohnADMIN", "Password123%");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AuthResponseDTO response = objectMapper.readValue(content, AuthResponseDTO.class);

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFullName("Test User");
        customerDTO.setPaymentMethod("pm_card_visa");
        customerDTO.setAddress("Holubi dum 25, Horni dolni  189 56");

        mockMvc.perform(MockMvcRequestBuilders.post("/vip")
                        .header("Authorization", "Bearer " + response.getToken())
                        .content(objectMapper.writeValueAsString(customerDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("Payment failed. You know, " +
                        "as administrator you cannot buy VIP membership.")));
    }
}
