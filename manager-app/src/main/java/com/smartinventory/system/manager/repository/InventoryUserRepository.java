package com.smartinventory.system.manager.repository;

import com.smartinventory.system.manager.entity.InventoryUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InventoryUserRepository extends CrudRepository<InventoryUser, Integer> {

    Optional<InventoryUser> findByUsername(String username);
}
