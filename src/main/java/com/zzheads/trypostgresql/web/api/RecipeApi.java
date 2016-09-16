package com.zzheads.trypostgresql.web.api;


import com.zzheads.trypostgresql.model.Recipe;
import com.zzheads.trypostgresql.model.MUser;
import com.zzheads.trypostgresql.service.CategoryService;
import com.zzheads.trypostgresql.service.RecipeService;
import com.zzheads.trypostgresql.service.MUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


/**
 * Created by alexeypapin on 04.09.16.
 */

@Controller
public class RecipeApi {
    private final RecipeService recipeService;
    private final CategoryService categoryService;
    private final MUserService userService;

    @Autowired
    public RecipeApi(RecipeService recipeService, CategoryService categoryService, MUserService userService) {
        this.recipeService = recipeService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    private MUser getLoggedUser() {
        return (MUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @RequestMapping(value = "/recipe", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody String addRecipe(@RequestBody String jsonString) {
        Recipe recipe = Recipe.fromJson(jsonString);
        recipe.setUser(getLoggedUser());
        if (recipe.getCategory() != null)
            recipe.setCategory(categoryService.findByName(recipe.getCategory().getName()));
        recipe.setId(recipeService.save(recipe));
        return recipe.toJson();
    }

    @RequestMapping(value = "/recipe", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus (HttpStatus.OK)
    public @ResponseBody String getAllRecipes() {
        List<Recipe> recipes = recipeService.findAll();
        return Recipe.toJson(recipes);
    }

    @RequestMapping(value = "/recipe_bycategory/{id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus (HttpStatus.OK)
    public @ResponseBody String getRecipesByCategory(@PathVariable Long id) {
        List<Recipe> recipes;
        if (categoryService.findById(id).getName().equals("Unassigned"))
            recipes = recipeService.findAll();
        else
            recipes = recipeService.findByCategory(categoryService.findById(id));
        return Recipe.toJson(recipes);
    }

    @RequestMapping(value = "/recipe_bypattern/{pattern}.{method}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus (HttpStatus.OK)
    public @ResponseBody String getRecipesByFirstLettersInSearch(@PathVariable String pattern, @PathVariable String method) {
        List<Recipe> recipes = recipeService.findByPattern(pattern, method);
        return Recipe.toJson(recipes);
    }

    @RequestMapping(value = "/recipe/{id}", method = RequestMethod.PUT, produces = {"application/json"})
    @ResponseStatus (HttpStatus.OK)
    public @ResponseBody String updateRecipe(@RequestBody String jsonString, @PathVariable Long id) {
        Recipe updatingRecipe = Recipe.fromJson(jsonString);
        if (updatingRecipe.getFavoriteUsers()!=null) {
            for (int i=0;i<updatingRecipe.getFavoriteUsers().size();i++) {
                MUser userWithOnlyUsername = updatingRecipe.getFavoriteUsers().get(i);
                MUser userWithProperties = userService.findByUsername(userWithOnlyUsername.getUsername());
                updatingRecipe.removeFavorite(userWithOnlyUsername);
                updatingRecipe.addFavorite(userWithProperties);
            }
        }
        updatingRecipe.setUser(userService.findByUsername(updatingRecipe.getUser().getUsername()));
        updatingRecipe.setPhoto(recipeService.findById(id).getPhoto());
        Recipe recipe = recipeService.findById(id);
        recipe.setProperties(updatingRecipe);
        recipeService.save(recipe);
        return recipe.toJson();
    }

    @RequestMapping(value = "/recipe/{id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus (HttpStatus.OK)
    public @ResponseBody String getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeService.findById(id);
        return recipe.toJson();
    }

    @RequestMapping(value = "/recipe/{id}", method = RequestMethod.DELETE, produces = {"application/json"})
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void deleteRecipeById(@PathVariable Long id, RedirectAttributes attributes) {
        Recipe recipe = recipeService.findById(id);

        if (recipe.getCategory() != null) {
            recipe.getCategory().removeRecipe(recipe);
            categoryService.save(recipe.getCategory());
        }
        recipeService.delete(recipe);
    }

}
