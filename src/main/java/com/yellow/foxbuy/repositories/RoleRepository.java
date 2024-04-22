package com.yellow.foxbuy.repositories;

import com.yellow.foxbuy.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findFirstByName(String name);

    Optional<Role> findRoleByName(String name);

}
