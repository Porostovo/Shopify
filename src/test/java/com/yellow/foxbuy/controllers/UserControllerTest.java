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
}