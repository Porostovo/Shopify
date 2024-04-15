package com.yellow.foxbuy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.*;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.ConfirmationTokenRepository;
import com.yellow.foxbuy.repositories.UserRepository;
import com.yellow.foxbuy.services.ConfirmationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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

    @Autowired
    public StripeControllerTest(MockMvc mockMvc, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        userRepository.deleteAll();
    }

    @Test
    public void processVipPayment() throws Exception {
        UserDTO userDTO = new UserDTO("user28", "email@email.com28", "Password123%");
        UserDTO userDTO2 = new UserDTO("user288", "email@email.com288", "Password123%");
        LoginRequest loginRequest = new LoginRequest("user288", "Password123%");

        List<User> users = userRepository.findAll();
        long initialCount = users.stream().filter(ads -> ads.getAddress() != null).count();

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO2))
                .contentType(MediaType.APPLICATION_JSON));


        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON));


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
        UserDTO userDTO = new UserDTO("user28", "email@email.com28", "Password123%");
        LoginRequest loginRequest = new LoginRequest("user28", "Password123%");

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON));


        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .content(objectMapper.writeValueAsString(loginRequest))
                .contentType(MediaType.APPLICATION_JSON));


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
                .andExpect(jsonPath("$.error", is("Payment failed. You know, as administrator you cannot" +
                        " buy VIP membership.")));
    }
}