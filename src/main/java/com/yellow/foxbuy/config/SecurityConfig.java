package com.yellow.foxbuy.config;

import com.yellow.foxbuy.filters.JwtAuthorisationFilter;
import com.yellow.foxbuy.models.ConfirmationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
   private final JwtAuthorisationFilter jwtAuthorisationFilter;

    @Autowired
    public SecurityConfig(JwtAuthorisationFilter jwtAuthorisationFilter) {
        this.jwtAuthorisationFilter = jwtAuthorisationFilter;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize ->
                        authorize.requestMatchers("/registration").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/confirm").permitAll()
                                .requestMatchers("/test").authenticated()
                                .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtAuthorisationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults());
        return http.build();
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
        return new ConfirmationToken();
    }

}

