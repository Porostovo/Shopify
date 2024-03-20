package com.yellow.foxbuy.filters;

import com.yellow.foxbuy.models.DTOs.LoginRequest;
import com.yellow.foxbuy.services.AuthenticationService;
import com.yellow.foxbuy.services.UserService;
import com.yellow.foxbuy.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthorisationFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;
    private UserService userService;
    private AuthenticationService authenticationService;
    @Autowired
    public JwtAuthorisationFilter(JwtUtil jwtUtil, UserService userService, AuthenticationService authenticationService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    public JwtAuthorisationFilter() {
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = getJwtFromRequest(request);
        if (token != null && jwtUtil.validateJwt(token) != null){
            String username = jwtUtil.getUsernameFromJWT(token);
            if (StringUtils.hasText(username)) {
                ResponseEntity<?> authenticationResponse = authenticationService.authenticateUser(new LoginRequest(username, null));
                if (authenticationResponse.getStatusCode().is2xxSuccessful()) {
                    SecurityContextHolder.getContext().setAuthentication((Authentication) authenticationResponse.getBody());
                } else {
                    // Handle authentication failure
                    response.sendError(authenticationResponse.getStatusCodeValue(), authenticationResponse.getBody().toString());
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


}