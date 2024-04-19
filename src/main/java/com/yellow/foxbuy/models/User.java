package com.yellow.foxbuy.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name="user_details")
public class  User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String email;
    private String password;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ConfirmationToken token;
    private Boolean verified;
    @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL)
    private List<Ad> ads = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    private String fullName;
    private String address;
    private String customerId;
    private LocalDateTime banned;
    private String refreshToken;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = false;
        this.banned = null;
    }
    public User(String username, String email, String password, Set<Role> roles  ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.banned = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public String getRole(){
        String userRole = "";
        for (Role role : roles) {
            userRole = role.getName();
        }
        return userRole.substring(5);
    }
}
