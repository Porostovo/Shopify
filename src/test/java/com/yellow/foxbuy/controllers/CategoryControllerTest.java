package com.yellow.foxbuy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.repositories.AdRepository;
import com.yellow.foxbuy.repositories.CategoryRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AdRepository adRepository;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        categoryRepository.deleteAll();
        adRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void listNotEmptyCategories() throws Exception {
        int initialCategoryCount = categoryRepository.findAll().size();
        Category category1 = new Category("name1", "description1");
        Category category2 = new Category("name2", "description2");
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        Ad ad1 = new Ad("ad1", category1);
        Ad ad2 = new Ad("ad2", category2);
        adRepository.save(ad1);
        adRepository.save(ad2);

        mockMvc.perform(MockMvcRequestBuilders.get("/category")
                .content(objectMapper.writeValueAsString(category1))
                .content(objectMapper.writeValueAsString(category2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].name", is("name1")))
                .andExpect(jsonPath("$[0].description", is("description1")))
                .andExpect(jsonPath("$[0].ads", is(1)))
                .andExpect(jsonPath("$[1].name", is("name2")))
                .andExpect(jsonPath("$[1].description", is("description2")))
                .andExpect(jsonPath("$[1].ads", is(1)));

        assertEquals(initialCategoryCount + 2, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void listNotEmptyCategories2() throws Exception {
        int initialCategoryCount = categoryRepository.findAll().size();
        Category category1 = new Category("name1", "description1");
        Category category2 = new Category("name2", "description2");
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        Ad ad1 = new Ad("ad1", category1);
        adRepository.save(ad1);

        mockMvc.perform(MockMvcRequestBuilders.get("/category")
                        .content(objectMapper.writeValueAsString(category1))
                        .content(objectMapper.writeValueAsString(category2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].name", is("name1")))
                .andExpect(jsonPath("$[0].description", is("description1")))
                .andExpect(jsonPath("$[0].ads", is(1)));

        assertEquals(initialCategoryCount + 2, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void listEmptyCategory() throws Exception {
        int initialCategoryCount = categoryRepository.findAll().size();
        Category category1 = new Category("name1", "description1");
        categoryRepository.save(category1);

        mockMvc.perform(MockMvcRequestBuilders.get("/category")
                        .content(objectMapper.writeValueAsString(category1))
                        .param("empty", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].name", is("name1")))
                .andExpect(jsonPath("$[0].description", is("description1")))
                .andExpect(jsonPath("$[0].ads", is(0)));

        assertEquals(initialCategoryCount + 1, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void noCategories() throws Exception {
        int initialCategoryCount = categoryRepository.findAll().size();

        mockMvc.perform(MockMvcRequestBuilders.get("/category"))
                .andExpect(status().is(200));

        assertEquals(initialCategoryCount, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void invalidParameter() throws Exception {
        int initialCategoryCount = categoryRepository.findAll().size();
        Category category1 = new Category("name1", "description1");
        categoryRepository.save(category1);

        mockMvc.perform(MockMvcRequestBuilders.get("/category")
                        .content(objectMapper.writeValueAsString(category1))
                        .param("empty", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("Invalid parameter")));

        assertEquals(initialCategoryCount + 1, categoryRepository.findAll().size());
    }
}