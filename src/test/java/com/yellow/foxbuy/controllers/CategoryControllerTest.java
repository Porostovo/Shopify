package com.yellow.foxbuy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.BanDTO;
import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.AdRepository;
import com.yellow.foxbuy.repositories.CategoryRepository;
import com.yellow.foxbuy.repositories.RoleRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        categoryRepository.deleteAll();
        adRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
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
    @WithMockUser(username = "user", roles = {"ADMIN"})
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
    @WithMockUser(username = "user", roles = {"ADMIN"})
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
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void noCategories() throws Exception {
        int initialCategoryCount = categoryRepository.findAll().size();

        mockMvc.perform(MockMvcRequestBuilders.get("/category"))
                .andExpect(status().is(200));

        assertEquals(initialCategoryCount, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
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

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void createNewCategory1() throws Exception {
        int initialAdCount = categoryRepository.findAll().size();

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");

        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .content(objectMapper.writeValueAsString(beverageCategory))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name", is("Beverage")))
                .andExpect(jsonPath("$.description", is("Buy some good beer.")));

        assertEquals(initialAdCount + 1, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void createNewCategory2() throws Exception {
        int initialAdCount = categoryRepository.findAll().size();

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .content(objectMapper.writeValueAsString(beverageCategory))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("This category name is already taken.")));

        assertEquals(initialAdCount + 1, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void createNewCategory3() throws Exception {
        int initialAdCount = categoryRepository.findAll().size();

        Category beverageCategory = new Category("Beverage", "");

        mockMvc.perform(MockMvcRequestBuilders.post("/category")
                        .content(objectMapper.writeValueAsString(beverageCategory))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.description", is("Description is required.")));

        assertEquals(initialAdCount, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void updateCategory1() throws Exception {
        int initialAdCount = categoryRepository.findAll().size();

        Category beverageCategory1 = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory1);

        Category beverageCategory = new Category("ggg", "fgg");
        long idtemp = categoryRepository.findFirstByName("Beverage").getId();
        int id = (int) idtemp;

        mockMvc.perform(MockMvcRequestBuilders.put("/category/" + id)
                        .content(objectMapper.writeValueAsString(beverageCategory))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name", is("ggg")))
                .andExpect(jsonPath("$.description", is("fgg")))
                .andExpect(jsonPath("$.id", is(id)));

        assertEquals(initialAdCount + 1, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void updateCategory2() throws Exception {
        int initialAdCount = categoryRepository.findAll().size();

        Category beverageCategory1 = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory1);
        Category beverageCategory2 = new Category("Beverage2", "Buy some good beer2.");
        categoryRepository.save(beverageCategory2);

        long idtemp = categoryRepository.findFirstByName("Beverage2").getId();
        int id = (int) idtemp;

        mockMvc.perform(MockMvcRequestBuilders.put("/category/" + id)
                        .content(objectMapper.writeValueAsString(beverageCategory1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("This category name is already taken.")));

        assertEquals(initialAdCount + 2, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void deleteCategory1() throws Exception {
        int initialAdCount = categoryRepository.findAll().size();

        Category beverageCategory1 = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory1);
        Long id = categoryRepository.findFirstByName("Beverage").getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/category/" + id))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.message", is("Category was deleted.")));

        assertEquals(initialAdCount + 1, categoryRepository.findAll().size());
        assertEquals(categoryRepository.findFirstByOrderByIdDesc().getName(), "Uncategorized");
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void deleteCategory2() throws Exception {
        int initialAdCount = categoryRepository.findAll().size();

        Category beverageCategory1 = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory1);
        Long id = categoryRepository.findFirstByName("Beverage").getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/category/" + (id)));
        mockMvc.perform(MockMvcRequestBuilders.delete("/category/" + (id)))
                .andExpect(jsonPath("$.error", is("This category id doesn't exist.")));

        assertEquals(initialAdCount + 1, categoryRepository.findAll().size());
        assertEquals(categoryRepository.findFirstByOrderByIdDesc().getName(), "Uncategorized");

        Long id2 = categoryRepository.findFirstByName("Uncategorized").getId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/category/" + (id2)))
                .andExpect(jsonPath("$.error", is("This category is not possible to delete")));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"ADMIN"})
    public void banUserSuccess() throws Exception {
        Role role = new Role("ROLE_USER");
        roleRepository.save(role);

        User user = new User("user", "email@gmail.com", "password1/");
        userRepository.save(user);

        BanDTO banDTO = new BanDTO(5);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + user.getId() + "/ban")
                        .content(objectMapper.writeValueAsString(banDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.username", is("user")));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"ADMIN"})
    public void banUserFailedNoUser() throws Exception {

        BanDTO banDTO = new BanDTO(5);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/b4533d98-5354-4f8b-8921-49feb093be03/ban")
                        .content(objectMapper.writeValueAsString(banDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("User does not exist")));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"ADMIN"})
    public void banUserFailedNoDuration() throws Exception {
        Role role = new Role("ROLE_USER");
        roleRepository.save(role);

        User user = new User("user", "email@gmail.com", "password1/");
        userRepository.save(user);

        BanDTO banDTO = new BanDTO(0);

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + user.getId() + "/ban")
                        .content(objectMapper.writeValueAsString(banDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("Wrong ban duration")));
    }
}



