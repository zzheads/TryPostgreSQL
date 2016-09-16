package com.zzheads.trypostgresql.dao;

import com.zzheads.trypostgresql.model.MUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MUserDao extends CrudRepository<MUser,Long> {
    MUser findByUsername(String username);
    MUser save(MUser user);
}
