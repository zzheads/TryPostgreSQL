package com.zzheads.trypostgresql.service;


import com.zzheads.trypostgresql.dao.MUserDao;
import com.zzheads.trypostgresql.dao.RecipeDao;
import com.zzheads.trypostgresql.dao.RoleDao;
import com.zzheads.trypostgresql.model.MUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MUserServiceImpl implements MUserService {
    private final MUserDao userDao;
    private final RoleDao roleDao;
    private final RecipeDao recipeDao;

    @Autowired
    public MUserServiceImpl(MUserDao userDao, RoleDao roleDao, RecipeDao recipeDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.recipeDao = recipeDao;
    }

    @Override
    public MUser findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public Long save(MUser user) {
        return userDao.save(user).getId();
    }

    @Override
    public List<MUser> findAll() {
        return (List<MUser>) userDao.findAll();
    }

    @Override
    public MUser findById(Long id) {
        return userDao.findOne(id);
    }

    @Override
    public void delete(MUser user) {
        userDao.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load user from the database (throw exception if not found)
        MUser user = userDao.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Return user object
        return user;
    }
}
