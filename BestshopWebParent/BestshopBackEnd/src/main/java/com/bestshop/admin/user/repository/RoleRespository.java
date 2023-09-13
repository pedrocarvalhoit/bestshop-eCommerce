package com.bestshop.admin.user.repository;

import com.bestshop.common.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRespository extends CrudRepository<Role,Integer> {
}
