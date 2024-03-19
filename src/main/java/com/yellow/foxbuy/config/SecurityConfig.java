package com.yellow.foxbuy.config;

import com.yellow.foxbuy.services.UserServiceImp;

import com.yellow.foxbuy.models.ConfirmationToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserAuthentication userAuthentication;

    private final UserServiceImp userServiceImp;

    @Autowired
    public SecurityConfig(UserAuthentication userAuthentication, UserServiceImp userServiceImp) {
        this.userAuthentication = userAuthentication;
        this.userServiceImp = userServiceImp;
    }


    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/registration").permitAll()


                                .requestMatchers("/login").permitAll()

                                .requestMatchers("/confirm").permitAll()

                                .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    //from previous app
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(userAuthentication);
        return authenticationManagerBuilder.build();
    }
    @Bean
    //This bean is responsible for handling authentication failures.
    public AuthenticationFailureHandler authenticationFailureHandler() {
        SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
        failureHandler.setDefaultFailureUrl("/login?error=true");
        return failureHandler;
    }


    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails user = User.builder()
                .username("a")
                .password(passwordEncoder().encode("b"))
                .roles("user")
                .build();

        return new InMemoryUserDetailsManager(user);

    }
    @Bean
    public ConfirmationToken confirmationToken() {
        return new ConfirmationToken(); // Or instantiate it using appropriate arguments if needed
    }
}

