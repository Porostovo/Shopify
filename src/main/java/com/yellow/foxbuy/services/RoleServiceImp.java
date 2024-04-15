package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.RoleRepository;
import com.yellow.foxbuy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;


@Service
public class RoleServiceImp implements RoleService{
@Autowired
    private final RoleRepository roleRepository;
@Autowired
    private final UserRepository userRepository;

    public RoleServiceImp(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Role getReferenceById(long l) {
        return roleRepository.getReferenceById(l);
    }

    @Override
    public void setVIPRoleToUser(User user) {
        Role roleUser = roleRepository.findFirstByName("ROLE_VIP");
        user.setRoles(new HashSet<>(Collections.singletonList(roleUser)));
        userRepository.save(user);
    }

    public Role findRoleByName(String name) {
        Optional<Role> optRole = roleRepository.findRoleByName(name);
        if (optRole.isPresent()){
            return optRole.get();
        } else return null;

    }
}
