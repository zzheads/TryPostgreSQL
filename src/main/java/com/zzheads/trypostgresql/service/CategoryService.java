package com.zzheads.trypostgresql.service;//


import com.zzheads.trypostgresql.model.Category;

import java.util.List;

// recipesite
// com.zzheads.recipesite.service created by zzheads on 27.08.2016.
//
public interface CategoryService {
    List<Category> findAll();
    Category findById(Long id);
    Category findByName(String name);
    void save(Category category);
    void delete(Category category);
}
