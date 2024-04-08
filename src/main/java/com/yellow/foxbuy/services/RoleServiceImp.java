package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImp implements RoleService{
@Autowired
    private final RoleRepository roleRepository;

    public RoleServiceImp(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getReferenceById(long l) {
        return roleRepository.getReferenceById(l);
    }

    @Override
    public Role findRoleByName(String name) {
        Optional<Role> optRole = roleRepository.findRoleByName(name);
        if (optRole.isPresent()){
            return optRole.get();
        } else return null;
    }
}
