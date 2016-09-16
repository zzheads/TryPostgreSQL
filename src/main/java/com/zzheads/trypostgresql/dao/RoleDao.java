package com.zzheads.trypostgresql.dao;

import com.zzheads.trypostgresql.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by alexeypapin on 06.09.16.
 */
@Repository
public interface RoleDao extends CrudRepository<Role, Long> {
}
