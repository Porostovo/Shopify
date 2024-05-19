package com.yellow.foxbuy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellow.foxbuy.config.SecurityConfig;
import com.yellow.foxbuy.models.*;
import com.yellow.foxbuy.models.ConfirmationToken;
import com.yellow.foxbuy.models.DTOs.*;
import com.yellow.foxbuy.repositories.*;
import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.ConfirmationTokenRepository;
import com.yellow.foxbuy.repositories.RoleRepository;
import com.yellow.foxbuy.repositories.UserRepository;
import com.yellow.foxbuy.services.ConfirmationTokenService;
import org.json.JSONObject;
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

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private ConfirmationTokenService confirmationTokenService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AdRepository adRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RatingRepository ratingRepository;


    private ObjectMapper objectMapper;
    //// fields REFRESH_TOKEN_NOT_IN_DATABASE, EXPIRED_JWT_TOKEN for "JohnUSER", "Password123%", "ROLE_USER"
    private static final String REFRESH_TOKEN_EXPIRED = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6IkpvaG5VU0VSIiwiZW1haWwiOiJlbWFpbEBlbWFpbC5jb20iLCJpYXQiOjE3MTM0NDQ1NjQsImV4cCI6MTcxMzQ0NDcxNH0.W5MQHDx3xFt--1o3BgVHY_a8tRSsWdp5wNKeaOE0NYI";
    private static final String EXPIRED_JWT_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6IkpvaG5VU0VSIiwiZW1haWwiOiJlbWFpbEBlbWFpbC5jb20iLCJhdXRob3JpdGllcyI6IlJPTEVfVVNFUiIsImlhdCI6MTcxMzQ0NDU2NCwiZXhwIjoxNzEzNDQ0NjI0fQ.AX2fDYBxrlwOutr5L29ejKVJvcMHauRpEGQGthjSeck";

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        userRepository.deleteAll();
        confirmationTokenRepository.deleteAll();
        roleRepository.deleteAll();
        ratingRepository.deleteAll();
        adRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void registrationSuccess() throws Exception {
        int initialUserCount = userRepository.findAll().size();

        UserDTO userDTO = new UserDTO("user", "email@email.com", "Password123%");

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.username", is("user")));

        assertEquals(initialUserCount + 1, userRepository.findAll().size());
    }

    @Test
    public void registrationFailedUserAlreadyExist() throws Exception {
        int initialUserCount = userRepository.findAll().size();

        UserDTO userDTO = new UserDTO("user", "email@email.com", "Password123%");
        UserDTO userDTO2 = new UserDTO("user", "something@email.com", "Password1234%");

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content(objectMapper.writeValueAsString(userDTO2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("Username already exists.")));

        assertEquals(initialUserCount + 1, userRepository.findAll().size());
    }

    @Test
    public void registrationFailedEmailAlreadyExist() throws Exception {
        int initialUserCount = userRepository.findAll().size();

        UserDTO userDTO = new UserDTO("user", "email@email.com", "Password123%");
        UserDTO userDTO2 = new UserDTO("user2", "email@email.com", "Password1234%");

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content(objectMapper.writeValueAsString(userDTO2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("Email is already used.")));

        assertEquals(initialUserCount + 1, userRepository.findAll().size());
    }

    @Test
    public void registrationFailedShortPassword() throws Exception {
        int initialUserCount = userRepository.findAll().size();

        UserDTO userDTO = new UserDTO("user", "email@email.com", "Pass1%");

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.password", is("Password must have at least 8 characters.")));

        assertEquals(initialUserCount, userRepository.findAll().size());
    }

    @Test
    public void registrationFailedWrongFormatPassword() throws Exception {
        int initialUserCount = userRepository.findAll().size();

        UserDTO userDTO = new UserDTO("user", "email@email.com", "Password1");

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.password", is("Password must have at least one uppercase, one lowercase, one number, and one special character (@ $ ! % * ? & . ,).")));

        assertEquals(initialUserCount, userRepository.findAll().size());
    }

    @Test
    public void registrationFailedInvalidEmail() throws Exception {
        int initialUserCount = userRepository.findAll().size();

        UserDTO userDTO = new UserDTO("user", "emailemail.com", "Password1");

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.email", is("Email should be valid.")));

        assertEquals(initialUserCount, userRepository.findAll().size());
    }

    @Test
    public void registrationFailedEmptyDetails() throws Exception {
        int initialUserCount = userRepository.findAll().size();

        UserDTO userDTO = new UserDTO();

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.email", is("Email is required.")))
                .andExpect(jsonPath("$.password", is("Password is required.")))
                .andExpect(jsonPath("$.username", is("Username is required.")));

        assertEquals(initialUserCount, userRepository.findAll().size());
    }

    @Test
    public void loginSuccess() throws Exception {

        UserDTO userDTO = new UserDTO("user", "email@email.com", "Password123%");
        LoginRequest loginRequest = new LoginRequest("user", "Password123%");

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.message", is("Login successful.")));
    }

    @Test
    public void loginFailedEmptyUsername() throws Exception {

        UserDTO userDTO = new UserDTO("user", "email@email.com", "Password123%");
        LoginRequest loginRequest = new LoginRequest("", "Password123%");

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.username", is("Username is required")));
    }

    @Test
    public void loginFailedEmptyPassword() throws Exception {

        UserDTO userDTO = new UserDTO("user", "email@email.com", "Password123%");
        LoginRequest loginRequest = new LoginRequest("user", "");

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.password", is("Password is required")));
    }

    @Test
    public void loginFailedWrongUsername() throws Exception {

        UserDTO userDTO = new UserDTO("user", "email@email.com", "Password123%");
        LoginRequest loginRequest = new LoginRequest("uuser", "Password123%");

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("Username or password are incorrect.")));
    }

    @Test
    public void loginFailedWrongPassword() throws Exception {

        UserDTO userDTO = new UserDTO("user", "email@email.com", "Password123%");
        LoginRequest loginRequest = new LoginRequest("user", "Password123%%");

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("Username or password are incorrect.")));
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void loginFailedUserBanned() throws Exception {

        UserDTO userDTO = new UserDTO("testUser", "email@email.com1", "Password123%");
        LoginRequest loginRequest = new LoginRequest("testUser", "Password123%%");

        BanDTO banDTO = new BanDTO(5);

        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/user/" + userRepository.findByUsername(userDTO.getUsername()).get().getId() + "/ban")
                .content(objectMapper.writeValueAsString(banDTO))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is("User is temporarily banned")));
    }


    @Test
    public void testVerificationEmailConfirmEndpoint() throws Exception {
        User user = new User("user", "emaile@mail.com", "Password1");
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        mockMvc.perform(MockMvcRequestBuilders.get("/confirm").param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Confirmed")));
        assertEquals(true, userRepository.findById(user.getId()).get().getVerified());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getUserDetailsSuccess() throws Exception {
        User user = new User("user", "email@email.com", "Password1");
        userRepository.save(user);
        Set<Role> roles = new HashSet<>();
        Role role = new Role("ROLE_USER");
        roles.add(role);
        roleRepository.save(role);
        user.setRoles(roles);
        userRepository.save(user);
        Category category = new Category("category1", "description1");
        categoryRepository.save(category);
        Ad ad1 = new Ad("Leviathan Axe", "Good axe to kill norse gods. Used, some scratches and blood marks.", 3000.00, "12345", user, category);
        Ad ad2 = new Ad("Blades of Chaos", "Good blades.", 3000.00, "12345", user, category);
        adRepository.save(ad1);
        adRepository.save(ad2);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", user.getId()))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.username", is("user")))
                .andExpect(jsonPath("$.email", is("email@email.com")))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.ads[0].title", is("Leviathan Axe")))
                .andExpect(jsonPath("$.ads[1].title", is("Blades of Chaos")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getUserDetailsFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/b1042633-c69e-4c4e-b14e-46e"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("User doesn't exist.")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void listUsersSuccess() throws Exception {
        for (int i = 0; i < 12; i++) {
            User user = new User("user", "email@email.com", "Password1");
            userRepository.save(user);
            Set<Role> roles = new HashSet<>();
            Role role = new Role("ROLE_USER");
            roles.add(role);
            roleRepository.save(role);
            user.setRoles(roles);
            userRepository.save(user);
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .param("page", "1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.total_pages", is(2)))
                .andExpect(jsonPath("$.users[0].username", is("user")))
                .andExpect(jsonPath("$.users[5].role", is("USER")))
                .andExpect(jsonPath("$.users[9].email", is("email@email.com")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void listUsersFailed() throws Exception {
        for (int i = 0; i < 2; i++) {
            User user = new User("user", "email@email.com", "Password1");
            userRepository.save(user);
            Set<Role> roles = new HashSet<>();
            Role role = new Role("ROLE_USER");
            roles.add(role);
            roleRepository.save(role);
            user.setRoles(roles);
            userRepository.save(user);
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .param("page", "2"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("This page is empty.")));
    }

    @Test
    public void identitySuccess() throws Exception {
        Role role = new Role("ROLE_ADMIN");
        roleRepository.save(role);

        UserDTO userDTO = new UserDTO("user", "email@email.com", "Password123%");
        LoginRequest loginRequest = new LoginRequest("user", "Password123%");

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

        mockMvc.perform(MockMvcRequestBuilders.post("/identity")
                        .header("Authorization", "Bearer " + response.getToken())
                        .content(objectMapper.writeValueAsString(response))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.username", is("user")));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void identityFailedNotValidToken() throws Exception {
        UserDTO userDTO = new UserDTO("user", "email@email.com", "Password123%");
        LoginRequest loginRequest = new LoginRequest("user", "Password123%");

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
        response.setToken("Wrong_Token");       //setting to invalid token

        mockMvc.perform(MockMvcRequestBuilders.post("/identity")
                        .content(objectMapper.writeValueAsString(response))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("token is not valid")));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void identityFailedNotUserInCurrentDatabase() throws Exception {
        UserDTO userDTO = new UserDTO("user", "email@email.com", "Password123%");
        LoginRequest loginRequest = new LoginRequest("user", "Password123%");

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
        userRepository.deleteAll();         //deletes user from repository so should not be able to find him

        mockMvc.perform(MockMvcRequestBuilders.post("/identity")
                        .content(objectMapper.writeValueAsString(response))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("token does not match any user")));
    }

    @Test
    public void refreshTokenSUCCESS() throws Exception {
        Role roleUser = roleRepository.save(new Role("ROLE_USER"));

        User user1 = new User("JohnUSER",
                "email@email.com",
                SecurityConfig.passwordEncoder().encode("Password123%"),
                new HashSet<>(Collections.singletonList(roleUser)));
        user1.setVerified(true);
        userRepository.save(user1);

        LoginRequest loginRequest = new LoginRequest("JohnUSER", "Password123%");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AuthResponseDTO response = objectMapper.readValue(content, AuthResponseDTO.class);

        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO();
        refreshTokenDTO.setRefreshToken(response.getRefreshToken());

        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.post("/refreshtoken")
                        .content(objectMapper.writeValueAsString(refreshTokenDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.refreshToken", is(refreshTokenDTO.getRefreshToken())))
                .andReturn();

        String responseContent = result2.getResponse().getContentAsString();
        JSONObject jsonResponse = new JSONObject(responseContent);
        String token = jsonResponse.getString("jwtToken");
        assertThat(token).isNotEmpty();
        // check if refresh token is properly saved in the database.
        assertEquals(user1.getUsername(),
                userRepository.findFirstByRefreshToken(refreshTokenDTO.getRefreshToken()).getUsername());
    }

    @Test
    public void refreshTokenNotInDatabase() throws Exception {
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO();
        refreshTokenDTO.setRefreshToken(REFRESH_TOKEN_EXPIRED + "thisStringMakeTokenInvalid");

        mockMvc.perform(MockMvcRequestBuilders.post("/refreshtoken")
                        .content(objectMapper.writeValueAsString(refreshTokenDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.error", is("Refresh token is not in database.")));
    }

    @Test
    public void refreshTokenEXPIRED() throws Exception {
        Role roleUser = roleRepository.save(new Role("ROLE_USER"));

        User user1 = new User("JohnUSER",
                "email@email.com",
                SecurityConfig.passwordEncoder().encode("Password123%"),
                new HashSet<>(Collections.singletonList(roleUser)));
        user1.setVerified(true);
        user1.setRefreshToken(REFRESH_TOKEN_EXPIRED);
        userRepository.save(user1);

        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO();
        refreshTokenDTO.setRefreshToken(REFRESH_TOKEN_EXPIRED);

         mockMvc.perform(MockMvcRequestBuilders.post("/refreshtoken")
                        .content(objectMapper.writeValueAsString(refreshTokenDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.error", is("Refresh token is not valid. Please make a new sign " +
                        "in request.")));
    }
    @Test
    public void JWTtokenEXPIRED() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .header("Authorization", "Bearer " + EXPIRED_JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Authentication failed, please send refresh token" +
                        " to renew JWT.")));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void getUserRatingSuccess() throws Exception {
        User user1 = new User("user1", "email@email.com", "Password123%");
        User user2 = new User("user2", "email2@email.com", "Password123%");
        userRepository.save(user1);
        userRepository.save(user2);

        ratingRepository.save(new Rating(4, "you are good", user1, user2.getId()));

        mockMvc.perform(MockMvcRequestBuilders.get("/user/"+ user1.getId() + "/rating"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.ratings.[0].rating", is(4)))
                .andExpect(jsonPath("$.ratings.[0].comment", is("you are good")))
                .andExpect(jsonPath("$.ratings.[0].reaction", is("No response yet")));
    }
    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void getUserRatingSuccessNoRatings() throws Exception {
        User user1 = new User("user1", "email@email.com", "Password123%");
        userRepository.save(user1);
        mockMvc.perform(MockMvcRequestBuilders.get("/user/"+ user1.getId() + "/rating"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.error", is("User has no ratings")));
    }
    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    public void getUserRatingFailedNoUser() throws Exception {
        User user1 = new User("user1", "email@email.com", "Password123%");
        userRepository.save(user1);
        UUID uuid = user1.getId();
        userRepository.deleteAll();
        mockMvc.perform(MockMvcRequestBuilders.get("/user/"+ uuid + "/rating"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("User does not exist")));
    }
    @Test
    @WithMockUser(username = "user2", roles = {"ADMIN"})
    public void postUserRatingSuccess() throws Exception {
        roleRepository.save(new Role("ROLE_ADMIN"));

        User user1 = new User("user1", "email@email.com", "Password123%");
        userRepository.save(user1);

        UserDTO userDTO = new UserDTO("user2", "email3@email.com", "Password123%");
        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON));

        LoginRequest loginRequest = new LoginRequest("user2", "Password123%");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AuthResponseDTO response = objectMapper.readValue(content, AuthResponseDTO.class);

        RatingDTO ratingDTO = new RatingDTO(4, "you are good");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/"+ user1.getId() + "/rating")
                        .header("Authorization", "Bearer " + response.getToken())
                        .content(objectMapper.writeValueAsString(ratingDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[\"Your rating was successful:\"].rating", is(4)))
                .andExpect(jsonPath("$[\"Your rating was successful:\"].comment", is("you are good")))
                .andExpect(jsonPath("$[\"Your rating was successful:\"].reaction", is("No response yet")));
    }
    @Test
    @WithMockUser(username = "user2", roles = {"ADMIN"})
    public void postUserRatingFailedAlreadyRated() throws Exception {

        roleRepository.save(new Role("ROLE_ADMIN"));
        User user1 = new User("user1", "email@email.com", "Password123%");
        userRepository.save(user1);

        UserDTO userDTO = new UserDTO("user2", "email3@email.com", "Password123%");
        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                .content(objectMapper.writeValueAsString(userDTO))
                .contentType(MediaType.APPLICATION_JSON));

        LoginRequest loginRequest = new LoginRequest("user2", "Password123%");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AuthResponseDTO response = objectMapper.readValue(content, AuthResponseDTO.class);

        //rating 1
        RatingDTO ratingDTO = new RatingDTO(4, "you are good");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/"+ user1.getId() + "/rating")
                        .header("Authorization", "Bearer " + response.getToken())
                        .content(objectMapper.writeValueAsString(ratingDTO))
                        .contentType(MediaType.APPLICATION_JSON));
        // rating 2
        mockMvc.perform(MockMvcRequestBuilders.post("/user/"+ user1.getId() + "/rating")
                        .header("Authorization", "Bearer " + response.getToken())
                        .content(objectMapper.writeValueAsString(ratingDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("You already sent rating to this user")));
    }

    @Test
    @WithMockUser(username = "user2", roles = {"ADMIN"})
    public void postUserRatingFailedRatingOwn() throws Exception {
        UserDTO userDTO = new UserDTO("user2", "email3@email.com", "Password123%");
        MvcResult result1 =  mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content1 = result1.getResponse().getContentAsString();
        Map<?,?> convert = objectMapper.readValue(content1, Map.class);
        String uuid = (String) convert.get("id");

        LoginRequest loginRequest = new LoginRequest("user2", "Password123%");
        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content2 = result2.getResponse().getContentAsString();
        AuthResponseDTO response2 = objectMapper.readValue(content2, AuthResponseDTO.class);

        RatingDTO ratingDTO = new RatingDTO(4, "you are good");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/"+ uuid + "/rating")
                        .header("Authorization", "Bearer " + response2.getToken())
                        .content(objectMapper.writeValueAsString(ratingDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("User cant comment on his own ad")));
    }
    @Test
    @WithMockUser(username = "user2", roles = {"ADMIN"})
    public void deleteUserRatingSuccess() throws Exception {
        roleRepository.save(new Role("ROLE_ADMIN"));
        User user1 = new User("user1", "email@email.com", "Password123%");
        userRepository.save(user1);

        UserDTO userDTO = new UserDTO("user2", "email3@email.com", "Password123%");
        MvcResult result1 =  mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content1 = result1.getResponse().getContentAsString();
        Map<?,?> convert = objectMapper.readValue(content1, Map.class);
        String uuid = (String) convert.get("id");

        LoginRequest loginRequest = new LoginRequest("user2", "Password123%");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AuthResponseDTO response = objectMapper.readValue(content, AuthResponseDTO.class);

        Rating rating =new Rating(5, "you are good",
                userRepository.getReferenceById(UUID.fromString(uuid)),user1.getId());
        ratingRepository.save(rating);

        mockMvc.perform(MockMvcRequestBuilders.delete("/user/" + uuid+"/rating/"+rating.getId())
                        .header("Authorization", "Bearer " + response.getToken()))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.rating", is(5)))
                .andExpect(jsonPath("$.comment", is("you are good")));
    }
    @Test
    @WithMockUser(username = "user2", roles = {"ADMIN"})
    public void deleteUserRatingFailRatingDontExist() throws Exception {
        roleRepository.save(new Role("ROLE_ADMIN"));
        User user1 = new User("user1", "email@email.com", "Password123%");
        userRepository.save(user1);

        UserDTO userDTO = new UserDTO("user2", "email3@email.com", "Password123%");
        MvcResult result1 =  mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content1 = result1.getResponse().getContentAsString();
        Map<?,?> convert = objectMapper.readValue(content1, Map.class);
        String uuid = (String) convert.get("id");

        LoginRequest loginRequest = new LoginRequest("user2", "Password123%");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AuthResponseDTO response = objectMapper.readValue(content, AuthResponseDTO.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/user/" + uuid+"/rating/0")
                        .header("Authorization", "Bearer " + response.getToken()))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("Rating does not exist")));
    }
    @Test
    @WithMockUser(username = "user2", roles = {"USER"})
    public void deleteUserRatingFailDeletingOtherUsersRating() throws Exception {
        Set<Role> roles = new HashSet<>();
        Role role = new Role("ROLE_USER");
        roles.add(role);
        roleRepository.save(role);

        User user1 = new User("user1", "email@email.com", "Password123%");
        user1.setRoles(roles);
        userRepository.save(user1);

        UserDTO userDTO = new UserDTO("user2", "email3@email.com", "Password123%");
        MvcResult result1 =  mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content(objectMapper.writeValueAsString(userDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content1 = result1.getResponse().getContentAsString();
        Map<?,?> convert = objectMapper.readValue(content1, Map.class);
        String uuid = (String) convert.get("id");

        LoginRequest loginRequest = new LoginRequest("user2", "Password123%");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        AuthResponseDTO response = objectMapper.readValue(content, AuthResponseDTO.class);

        Rating rating =new Rating(5, "you are good",
                userRepository.getReferenceById(UUID.fromString(uuid)),user1.getId());
        ratingRepository.save(rating);

        mockMvc.perform(MockMvcRequestBuilders.delete("/user/" + user1.getId() +"/rating/"+rating.getId())
                        .header("Authorization", "Bearer " + response.getToken()))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("You cannot delete rating of another user")));
    }
    @Test
    @WithMockUser(username = "user2", roles = {"ADMIN"})
    public void postCommentUserRatingSuccess() throws Exception {
        roleRepository.save(new Role("ROLE_ADMIN"));
        User user1 = new User("user1", "email@email.com", "Password123%");
        userRepository.save(user1);
        User user2 = new User("user2", "email@email.com", "Password123%");
        userRepository.save(user2);

        Rating rating =new Rating(5, "you are good", user2, user1.getId());
        ratingRepository.save(rating);
        RatingResponseDTO ratingResponseDTO = new RatingResponseDTO("thank you");

        mockMvc.perform(MockMvcRequestBuilders.post("/user/rating/" + rating.getId())
                        .content(objectMapper.writeValueAsString(ratingResponseDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.reaction", is("thank you")));
    }

    @Test
    @WithMockUser(username = "user2", roles = {"ADMIN"})
    public void postCommentUserRatingFailOtherUser() throws Exception {
        roleRepository.save(new Role("ROLE_ADMIN"));
        User user1 = new User("user1", "email@email.com", "Password123%");
        userRepository.save(user1);
        User user2 = new User("user2", "email@email.com", "Password123%");
        userRepository.save(user2);

        Rating rating =new Rating(5, "you are good", user1, user2.getId());
        ratingRepository.save(rating);

        RatingResponseDTO ratingResponseDTO = new RatingResponseDTO("thank you");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/rating/" + rating.getId())
                        .content(objectMapper.writeValueAsString(ratingResponseDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("You  cannot respond to rating of another user")));
    }
    @Test
    @WithMockUser(username = "user2", roles = {"ADMIN"})
    public void postCommentUserRatingFailBadId() throws Exception {
        roleRepository.save(new Role("ROLE_ADMIN"));
        User user1 = new User("user1", "email@email.com", "Password123%");
        userRepository.save(user1);
        User user2 = new User("user2", "email@email.com", "Password123%");
        userRepository.save(user2);

        ratingRepository.deleteAll();

        RatingResponseDTO ratingResponseDTO = new RatingResponseDTO("thank you");
        mockMvc.perform(MockMvcRequestBuilders.post("/user/rating/0")
                        .content(objectMapper.writeValueAsString(ratingResponseDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("Comment does not exist")));
    }
}
