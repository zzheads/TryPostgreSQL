package com.zzheads.trypostgresql.service;//


import com.zzheads.trypostgresql.model.Category;
import com.zzheads.trypostgresql.model.MUser;
import com.zzheads.trypostgresql.model.Recipe;

import java.util.List;

// RecipeSite
// com.zzheads.RecipeSite.service created by zzheads on 26.08.2016.
//
public interface RecipeService {
    List<Recipe> findAll();
    Recipe findById(Long recipeId);
    void delete(Long id);
    void delete(Recipe recipe);
    Long save(Recipe recipe);
    List<Recipe> findAll(MUser currentUser);
    void addToCategory(Recipe recipe, Category category);
    List<Recipe> findByCategory(Category category);
    List<Boolean> getRecipesFavorites(List<Recipe> recipes, MUser loggedUser);
    List<Recipe> findByPattern(String pattern, String method);
}
