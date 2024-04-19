package com.yellow.foxbuy.config;

import com.yellow.foxbuy.filters.JwtAuthorisationFilter;
import com.yellow.foxbuy.models.ConfirmationToken;
import com.yellow.foxbuy.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableWebSecurity
@EnableScheduling
public class SecurityConfig {
    @Autowired
    private JwtAuthorisationFilter jwtAuthorisationFilter;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsServiceImpl userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        return daoAuthenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        //.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/registration").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/confirm").permitAll()
                        .requestMatchers("/refreshtoken").permitAll()
                        .requestMatchers("/category/**").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/vip").hasAnyRole("USER", "VIP_USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/category/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/category/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/category/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/category/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger").permitAll()
                        .requestMatchers(HttpMethod.GET, "/advertisement/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/advertisement/{id}/message").permitAll()
                        .requestMatchers(HttpMethod.POST, "/advertisement/**").hasAnyRole("USER", "VIP", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/advertisement/**").hasAnyRole("USER", "VIP", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/advertisement/**").hasAnyRole("USER", "VIP", "ADMIN")
                        .requestMatchers(HttpMethod.POST,"/user/**").hasRole("ADMIN")
                        .requestMatchers("/logs").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/test").hasAnyRole("USER", "VIP", "ADMIN")
                        .requestMatchers("/refreshtoken").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtAuthorisationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptions) -> exceptions.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public ConfirmationToken confirmationToken() {
        return new ConfirmationToken();
    }
}

