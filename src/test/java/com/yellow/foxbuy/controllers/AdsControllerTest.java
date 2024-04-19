package com.yellow.foxbuy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellow.foxbuy.config.SecurityConfig;
import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.Category;
import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.AdRepository;
import com.yellow.foxbuy.repositories.CategoryRepository;
import com.yellow.foxbuy.repositories.RoleRepository;
import com.yellow.foxbuy.repositories.UserRepository;
import com.yellow.foxbuy.services.BanService;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BanService banService;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        categoryRepository.deleteAll();
        adRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void adCreatedSuccess() throws Exception {
        int initialAdCount = adRepository.findAll().size();

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        AdDTO adDTO = new AdDTO("Pilsner urquell", "Tasty beer.", 3000.00, "12345", beverageCategory.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/advertisement")
                        .content(objectMapper.writeValueAsString(adDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.title", is("Pilsner urquell")))
                .andExpect(jsonPath("$.description", is("Tasty beer.")))
                .andExpect(jsonPath("$.price", is(3000.00)))
                .andExpect(jsonPath("$.zipcode", is("12345")))
                .andExpect(jsonPath("$.categoryID", is(beverageCategory.getId().intValue())));

        assertEquals(initialAdCount + 1, adRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void adCreatedError() throws Exception {

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        AdDTO adDTO = new AdDTO("Pilsner urquell", "Tasty beer.", 3000.00, "12345", 12355L);

        mockMvc.perform(MockMvcRequestBuilders.post("/advertisement")
                        .content(objectMapper.writeValueAsString(adDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("Category not found.")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void adCreatedFailed() throws Exception {

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        AdDTO adDTO = new AdDTO("", "Tasty beer.", 3000.00, "12345", beverageCategory.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/advertisement")
                        .content(objectMapper.writeValueAsString(adDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.title", is("Title is required!")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void adUpdateSuccess() throws Exception {

        User user = new User("user", "email@email.com", "Password1");
        userRepository.save(user);
        Set<Role> roles = new HashSet<>();
        Role role = new Role("ROLE_USER");
        roles.add(role);
        roleRepository.save(role);
        user.setRoles(roles);
        userRepository.save(user);

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        Ad ad = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user, beverageCategory);
        adRepository.save(ad);

        AdDTO adDTO2 = new AdDTO("Budweiser", "Less tasty beer.", 2000.00, "54321", beverageCategory.getId());

        mockMvc.perform(MockMvcRequestBuilders.put("/advertisement/{id}", ad.getId())
                        .content(objectMapper.writeValueAsString(adDTO2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.title", is("Budweiser")))
                .andExpect(jsonPath("$.description", is("Less tasty beer.")))
                .andExpect(jsonPath("$.price", is(2000.00)))
                .andExpect(jsonPath("$.zipcode", is("54321")))
                .andExpect(jsonPath("$.categoryID", is(beverageCategory.getId().intValue())));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void adUpdatedError() throws Exception {

        Role roleUser = roleRepository.save(new Role("ROLE_USER"));
        User user1 = new User("JohnUSER",
                "email@email.com",
                SecurityConfig.passwordEncoder().encode("password"),
                new HashSet<>(Collections.singletonList(roleUser)));
        user1.setVerified(true);
        userRepository.save(user1);

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        Ad ad = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user1, beverageCategory);
        adRepository.save(ad);

        mockMvc.perform(MockMvcRequestBuilders.put("/advertisement/{id}", ad.getId())
                        .content(objectMapper.writeValueAsString(ad))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("You are not authorized to update this advertisement")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void adDeleteSuccess() throws Exception {

        User user = new User("user", "email@email.com", "Password1");
        userRepository.save(user);
        Set<Role> roles = new HashSet<>();
        Role role = new Role("ROLE_USER");
        roles.add(role);
        roleRepository.save(role);
        user.setRoles(roles);
        userRepository.save(user);

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        Ad ad = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user, beverageCategory);
        adRepository.save(ad);
        int initialAdCount = adRepository.findAll().size();

        mockMvc.perform(MockMvcRequestBuilders.delete("/advertisement/{id}", ad.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.message", is("Your ad was deleted")));

        assertEquals(initialAdCount - 1, adRepository.findAll().size());

    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void adDeleteError() throws Exception {

        User user = new User("user", "email@email.com", "Password1");
        userRepository.save(user);
        Set<Role> roles = new HashSet<>();
        Role role = new Role("ROLE_USER");
        roles.add(role);
        roleRepository.save(role);
        user.setRoles(roles);
        userRepository.save(user);

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        Ad ad = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user, beverageCategory);
        adRepository.save(ad);
        mockMvc.perform(MockMvcRequestBuilders.delete("/advertisement/{id}", ad.getId())
                        .content(objectMapper.writeValueAsString(ad))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("You are not authorized to delete this advertisement")));

    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getAdvertisementSuccess() throws Exception {
        int initialAdCount = adRepository.findAll().size();

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        User user = new User("user", "user@email.cz", "Password1*");
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
    void getAdvertisementErrorAdHidden() throws Exception {
        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        User user = new User("user", "user@email.cz","Password1*");
        userRepository.save(user);

        Ad ad = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user, beverageCategory);
        ad.setHidden(true); //as if the owner of the ad is banned
        adRepository.save(ad);


        mockMvc.perform(MockMvcRequestBuilders.get("/advertisement/" + ad.getId()))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("Ad with this id doesn't exist.")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void listAdsUserSuccess() throws Exception {

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        User user = new User("user1", "user@email.cz", "Password1*");
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

        User user = new User("user1", "user@email.cz", "Password1*");
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

        User user = new User("user1", "user@email.cz", "Password1*");
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

    @Test
    @WithMockUser(username = "JohnUSER")
    public void sendMessageToSeller() throws Exception {
        Role roleUser = roleRepository.save(new Role("ROLE_USER"));

        User user = new User("JohnUSER",
                "email@email.com",
                SecurityConfig.passwordEncoder().encode("password"),
                new HashSet<>(Collections.singletonList(roleUser)));
        user.setVerified(true);
        userRepository.save(user);

        User user2 = new User("JohnUSER2",
                "email2@email2.com",
                SecurityConfig.passwordEncoder().encode("password"),
                new HashSet<>(Collections.singletonList(roleUser)));
        user.setVerified(true);
        userRepository.save(user2);

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        Ad ad = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user, beverageCategory);
        adRepository.save(ad);

        Ad ad2 = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user2, beverageCategory);
        adRepository.save(ad2);

        String inputJSONMessage = "{\n" +
                "        \"message\": \"test\"\n" +
                "    }";
        mockMvc.perform(MockMvcRequestBuilders.post("/advertisement/{id}/message", ad2.getId())
                        .content(inputJSONMessage)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.status", is("200")))
                .andExpect(jsonPath("$.message", is("Thank you for your message.")));
    }

    @Test
    @WithMockUser(username = "user")
    public void sendMessageToSeller2() throws Exception {

        User user = new User("user", "email@email.com", "Password1");
        userRepository.save(user);
        Set<Role> roles = new HashSet<>();
        Role role = new Role("ROLE_USER");
        roles.add(role);
        roleRepository.save(role);
        user.setRoles(roles);
        userRepository.save(user);

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        Ad ad = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user, beverageCategory);
        adRepository.save(ad);

        String inputJSONMessage = "{\n" +
                "        \"message\": \"test\"\n" +
                "    }";
        mockMvc.perform(MockMvcRequestBuilders.post("/advertisement/{id}/message", ad.getId())
                        .content(inputJSONMessage)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("You cannot write a message to your advertisements.")));
    }

    @Test
    public void sendMessageToSeller3() throws Exception {
        Role roleUser = roleRepository.save(new Role("ROLE_USER"));

        User user = new User("JohnUSER",
                "email@email.com",
                SecurityConfig.passwordEncoder().encode("password"),
                new HashSet<>(Collections.singletonList(roleUser)));
        user.setVerified(true);
        userRepository.save(user);

        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        Ad ad = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user, beverageCategory);
        adRepository.save(ad);

        String inputJSONMessage = "{\n" +
                "        \"message\": \"test\"\n" +
                "    }";
        mockMvc.perform(MockMvcRequestBuilders.post("/advertisement/{id}/message", ad.getId())
                        .content(inputJSONMessage)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("If you want send messages you have to be logged in.")));
    }

    @WithMockUser(username = "user", roles = "USER")
    void listAdsUserSuccessWithBannedUser() throws Exception {
        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);
        Long categoryId = beverageCategory.getId();

        User user = new User("user1", "user@email.cz","Password1*");
        userRepository.save(user);
        User user2 = new User("user2", "user2@email.cz","Password1*");
        userRepository.save(user2);

        Ad ad1 = new Ad("Pilsner urquell", "Tasty beer.", 3000.00, "12345", user, beverageCategory);
        Ad ad2 = new Ad("Pilsner urquell2", "Tasty beer2.", 3000.00, "12345", user2, beverageCategory);
        Ad ad3 = new Ad("Pilsner urquell3", "Tasty beer3.", 1000.00, "67890", user, beverageCategory);
        adRepository.save(ad1);
        adRepository.save(ad2);
        adRepository.save(ad3);

        banService.banUser(user2.getId(), 5);

        mockMvc.perform(MockMvcRequestBuilders.get("/advertisement")
                        .param("category", String.valueOf(categoryId)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.ads.[0].title", is("Pilsner urquell")))
                .andExpect(jsonPath("$.ads.[0].description", is("Tasty beer.")))
                .andExpect(jsonPath("$.ads.[0].price", is(3000.00)))
                .andExpect(jsonPath("$.ads.[0].zipcode", is("12345")))
                .andExpect(jsonPath("$.ads.[1].title", is("Pilsner urquell3")))
                .andExpect(jsonPath("$.ads.[1].description", is("Tasty beer3.")))
                .andExpect(jsonPath("$.ads.[1].price", is(1000.00)))
                .andExpect(jsonPath("$.ads.[1].zipcode", is("67890")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void listAdsUserFailSearchWithBannedUser() throws Exception {
        Category beverageCategory = new Category("Beverage", "Buy some good beer.");
        categoryRepository.save(beverageCategory);

        User user = new User("user1", "user@email.cz","Password1*");
        userRepository.save(user);

        banService.banUser(user.getId(), 5);

        mockMvc.perform(MockMvcRequestBuilders.get("/advertisement")
                        .param("user", user.getUsername()))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("User with this name doesn't exist.")));

    }
}
