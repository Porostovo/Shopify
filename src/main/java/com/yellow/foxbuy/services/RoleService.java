package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Role;
import com.yellow.foxbuy.models.User;

public interface RoleService{
    Role getReferenceById(long l);

    void setVIPRoleToUser(User user);
}
