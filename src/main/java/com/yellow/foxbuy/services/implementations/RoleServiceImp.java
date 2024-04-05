package com.yellow.foxbuy.services.implementations;

import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.repositories.RoleRepository;
import com.yellow.foxbuy.services.interfaces.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImp implements RoleService {
@Autowired
    private final RoleRepository roleRepository;

    public RoleServiceImp(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getReferenceById(long l) {
        return roleRepository.getReferenceById(l);
    }
}
