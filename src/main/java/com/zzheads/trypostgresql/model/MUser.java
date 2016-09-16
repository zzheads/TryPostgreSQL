package com.zzheads.trypostgresql.model;

import com.google.gson.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class MUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(length = 100)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    private List<Recipe> favoriteRecipes;

    public MUser () {}

    public MUser(String name) {
        this.username = name;
    }

    public MUser(Long id, String username, String password, boolean enabled, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Recipe> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public void setFavoriteRecipes(List<Recipe> favoriteRecipes) {
        this.favoriteRecipes = favoriteRecipes;
    }

    public void addFavorite (Recipe recipe) {
        if (favoriteRecipes == null)
            favoriteRecipes = new ArrayList<>();
        if (!favoriteRecipes.contains(recipe))
            favoriteRecipes.add(recipe);
    }

    public void removeFavorite (Recipe recipe) {
        if (favoriteRecipes != null && favoriteRecipes.contains(recipe)) favoriteRecipes.remove(recipe);
    }

    public boolean isFavorite (Recipe recipe) {
        if (favoriteRecipes == null) return false;
        return (favoriteRecipes.contains(recipe));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MUser)) return false;
        MUser user = (MUser) o;
        return getId() != null ? getId().equals(user.getId()) : user.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public void setProperties(MUser properties) {
        this.username = properties.getUsername();
        this.password = properties.getPassword();
        this.enabled = properties.isEnabled();
        this.role = properties.getRole();
    }

    private static class UserSerializer implements JsonSerializer<MUser> {
        @Override
        public JsonElement serialize(MUser src, Type typeOfSrc, JsonSerializationContext context) {
            return jsonObject(src);
        }
    }

    private static class ListUserSerializer implements JsonSerializer<List<MUser>> {
        @Override
        public JsonElement serialize(List<MUser> src, Type typeOfSrc, JsonSerializationContext context) {
            if (src!=null && src.size()>0) {
                JsonArray result = new JsonArray();
                for (MUser user : src) {
                    JsonObject object = jsonObject(user);
                    result.add(object);
                }
                return result;
            }
            return null;
        }
    }

    private static JsonObject jsonObject (MUser user) {
        JsonObject result = new JsonObject();
        result.addProperty("id", user.getId());
        result.addProperty("username", user.getUsername());
        result.addProperty("password", user.getPassword());
        result.addProperty("enabled", user.isEnabled());
        if (user.getFavoriteRecipes()!=null && user.getFavoriteRecipes().size()>0) {
            JsonArray jsonFavRecipes = new JsonArray();
            for (Recipe r : user.getFavoriteRecipes()) {
                JsonPrimitive jsonFavRecipe = new JsonPrimitive(r.getName());
                jsonFavRecipes.add(jsonFavRecipe);
            }
            result.add("favoriteRecipes", jsonFavRecipes);
        }
        JsonObject jsonRole = new JsonObject();
        jsonRole.addProperty("id", user.getRole().getId());
        jsonRole.addProperty("name", user.getRole().getName());
        result.add("role", jsonRole);
        if (user.getAuthorities()!=null && user.getAuthorities().size()>0) {
            JsonArray jsonAuthorities = new JsonArray();
            for (GrantedAuthority authority : user.getAuthorities()) {
                jsonAuthorities.add(new JsonPrimitive(authority.getAuthority()));
            }
            result.add("authorities", jsonAuthorities);
        }
        return result;
    }

    private static class UserDeserializer implements JsonDeserializer<MUser> {
        @Override
        public MUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            MUser result = new MUser();
            JsonObject object = json.getAsJsonObject();
            if (object.get("id")!=null) result.setId(object.get("id").getAsLong());
            if (object.get("username")!=null) result.setUsername(object.get("username").getAsString());
            if (object.get("password")!=null) result.setPassword(object.get("password").getAsString());
            if (object.get("enabled")!=null) result.setEnabled(object.get("enabled").getAsBoolean());
            if (object.get("favoriteRecipes")!=null) {
                for (JsonElement je : object.get("favoriteRecipes").getAsJsonArray()) {
                    result.addFavorite(new Recipe(je.getAsJsonPrimitive().getAsString()));
                }
            }
            if (object.get("role")!=null) {
                JsonObject jsonRole = object.get("role").getAsJsonObject();
                Role role = new Role();
                if (jsonRole.get("id")!=null) role.setId(jsonRole.get("id").getAsLong());
                if (jsonRole.get("name")!=null) role.setName(jsonRole.get("name").getAsString());
                result.setRole(role);
            }
            return result;
        }
    }

    public String toJson() {
        Gson gson = new GsonBuilder().registerTypeAdapter(MUser.class, new UserSerializer()).setPrettyPrinting().create();
        return gson.toJson(this, MUser.class);
    }

    public static MUser fromJson (String jsonString) {
        Gson gson = new GsonBuilder().registerTypeAdapter(MUser.class, new UserDeserializer()).setPrettyPrinting().create();
        return gson.fromJson(jsonString, MUser.class);
    }

    public static String toJson(List<MUser> users) {
        Gson gson = new GsonBuilder().registerTypeAdapter(List.class, new ListUserSerializer()).setPrettyPrinting().create();
        return gson.toJson(users, List.class);
    }

}
