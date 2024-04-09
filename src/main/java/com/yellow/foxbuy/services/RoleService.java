package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;

import java.util.Optional;

public interface RoleService{
    Role getReferenceById(long l);
    void setVIPRoleToUser(User user);
    Role findRoleByName(String name);

}
