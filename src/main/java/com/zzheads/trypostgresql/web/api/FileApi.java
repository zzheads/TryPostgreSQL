package com.zzheads.trypostgresql.web.api;


import com.zzheads.trypostgresql.model.Recipe;
import com.zzheads.trypostgresql.service.RecipeService;
import com.zzheads.trypostgresql.utils.NoPhoto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by alexeypapin on 07.09.16.
 */
@Controller
public class FileApi {
    private final RecipeService recipeService;
    private final NoPhoto NO_PHOTO = new NoPhoto();

    @Autowired
    public FileApi(RecipeService recipeService) throws IOException {
        this.recipeService = recipeService;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("128KB");
        factory.setMaxRequestSize("128KB");
        return factory.createMultipartConfig();
    }

    @RequestMapping(value="/upload/{recipeId}", method=RequestMethod.POST)
    public @ResponseBody String uploadRecipePhoto(@RequestParam("file") MultipartFile file, @PathVariable Long recipeId, HttpServletRequest request, RedirectAttributes attributes) throws IOException {
        Recipe recipe = recipeService.findById(recipeId);
        recipe.setPhoto(file.getBytes());
        recipeService.save(recipe);
        return null;
    }

    @RequestMapping(value="/photos/{recipeId}", method=RequestMethod.GET)
    public @ResponseBody byte[] getPhoto(@PathVariable Long recipeId) throws IOException {
        byte[] photo = recipeService.findById(recipeId).getPhoto();
        if (photo == null)
            return NO_PHOTO.getIMAGE();
        else
            return photo;
    }

}
