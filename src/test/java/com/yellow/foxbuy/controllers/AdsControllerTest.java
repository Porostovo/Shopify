package com.yellow.foxbuy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.AdRepository;
import com.yellow.foxbuy.repositories.CategoryRepository;
import com.yellow.foxbuy.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class AdsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AdRepository adRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        categoryRepository.deleteAll();
        adRepository.deleteAll();
    }

  @Test
  @WithMockUser(username = "user", roles = "USER")
    public void adCreatedSuccess() throws Exception {
        int initialAdCount = adRepository.findAll().size();

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        User user = new User("user", "user@email.cz","Password1*");
        userRepository.save(user);

        Ad ad= new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345");
        adRepository.save(ad);

        mockMvc.perform(MockMvcRequestBuilders.post("/advertisement")
                        .content(objectMapper.writeValueAsString(ad))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Pilsner urquell")))
                .andExpect(jsonPath("$.description", is("Tasty beer.")))
                .andExpect(jsonPath("$.price", is(3000.00)))
                .andExpect(jsonPath("$.zipcode", is(12345)))
                .andExpect(jsonPath("$.categoryID", is(2L)));

        assertEquals(initialAdCount + 1, adRepository.findAll().size());
    }
}
