package com.zzheads.trypostgresql.service;


import com.zzheads.trypostgresql.model.MUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface MUserService extends UserDetailsService {
    MUser findByUsername(String username);
    Long save(MUser user);
    List<MUser> findAll();
    MUser findById(Long id);
    void delete(MUser user);
}
