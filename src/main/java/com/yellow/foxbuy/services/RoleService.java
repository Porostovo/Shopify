package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;


public interface RoleService {

    void setVIPRoleToUser(User user);

    Role findRoleByName(String name);

}
