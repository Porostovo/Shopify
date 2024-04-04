package com.yellow.foxbuy.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.AdDTO;
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
        long id = beverageCategory.getId();
        int catId = (int) id;

       // User user = new User("user", "user@email.cz","Password1*");
       // userRepository.save(user);

        AdDTO addto = new AdDTO("Pilsner urquell", "Tasty beer.", 3000.00, "12345",id);


        mockMvc.perform(MockMvcRequestBuilders.post("/advertisement")
                        .content(objectMapper.writeValueAsString(addto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
               //.andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Pilsner urquell")))
                .andExpect(jsonPath("$.description", is("Tasty beer.")))
                .andExpect(jsonPath("$.price", is(3000.00)))
                .andExpect(jsonPath("$.zipcode", is("12345")))
               .andExpect(jsonPath("$.categoryID", is(catId)));

        Ad ad = adRepository.findById(20L).orElseThrow();
      System.out.println(ad.getTitle() + ad.getId());
      System.out.println("XXXXXXX"+ad.getUser());

        assertEquals(initialAdCount + 1, adRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getAdvertisementSuccess() throws Exception {
        int initialAdCount = adRepository.findAll().size();

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        User user = new User("user", "user@email.cz","Password1*");
        userRepository.save(user);

        Ad ad = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user, beverageCategory);
        adRepository.save(ad);

        mockMvc.perform(MockMvcRequestBuilders.get("/advertisement/{id}", ad.getId()))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.title", is("Pilsner urquell")))
                .andExpect(jsonPath("$.description", is("Tasty beer.")))
                .andExpect(jsonPath("$.price", is(3000.00)))
                .andExpect(jsonPath("$.zipcode", is("12345")));

        assertEquals(initialAdCount + 1, adRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getAdvertisementError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/advertisement/50"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("Ad with this id doesn't exist.")));

    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void listAdsUserSuccess() throws Exception {

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        User user = new User("user1", "user@email.cz","Password1*");
        userRepository.save(user);

        Ad ad1 = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user, beverageCategory);
        Ad ad2 = new Ad("Pilsner urquell2", "Tasty beer2.", 1000.00, "67890", user, beverageCategory);
        adRepository.save(ad1);
        adRepository.save(ad2);

        mockMvc.perform(MockMvcRequestBuilders.get("/advertisement")
                .param("user", "user1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].title", is("Pilsner urquell")))
                .andExpect(jsonPath("$[0].description", is("Tasty beer.")))
                .andExpect(jsonPath("$[0].price", is(3000.00)))
                .andExpect(jsonPath("$[0].zipcode", is("12345")))
                .andExpect(jsonPath("$[1].title", is("Pilsner urquell2")))
                .andExpect(jsonPath("$[1].description", is("Tasty beer2.")))
                .andExpect(jsonPath("$[1].price", is(1000.00)))
                .andExpect(jsonPath("$[1].zipcode", is("67890")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void listAdsUserFailed() throws Exception {

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        User user = new User("user1", "user@email.cz","Password1*");
        userRepository.save(user);

        Ad ad1 = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user, beverageCategory);
        adRepository.save(ad1);

        mockMvc.perform(MockMvcRequestBuilders.get("/advertisement")
                        .param("user", "user2"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("User with this name doesn't exist.")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void listAdsEmptyPage() throws Exception {
        Category category = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(category);
        Long categoryId = category.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/advertisement")
                        .param("page", "20")
                        .param("category", String.valueOf(categoryId)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("This page is empty.")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void listAdsCategoryDoesntExist() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/advertisement")
                        .param("category", "50"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("Category with this ID doesn't exist.")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void listAdsByPageAndCategory() throws Exception {
        Category category = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(category);
        Long categoryId = category.getId();

        User user = new User("user1", "user@email.cz","Password1*");
        userRepository.save(user);

        for (int i = 0; i < 12; i++) {
            adRepository.save(new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user, category));
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/advertisement")
                        .param("category", String.valueOf(categoryId))
                        .param("page", "1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.total_pages", is(2)));
    }
}
