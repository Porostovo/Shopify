package com.yellow.foxbuy.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    private String fullName;
    private String address;
    private String customerId;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.verified = false;
    }
    public User(String username, String email, String password, Set<Role> roles  ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }


    //private Set<String> roles;

    // Other fields, constructor, and getter/setter methods

    // Implement getAuthorities() method to return user roles as GrantedAuthority objects
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


}
