package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Role;

import java.util.Optional;

public interface RoleService{
    Role getReferenceById(long l);
    Role findRoleByName(String name);
}
