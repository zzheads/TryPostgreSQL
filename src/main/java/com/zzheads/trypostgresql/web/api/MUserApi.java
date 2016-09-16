package com.zzheads.trypostgresql.web.api;


import com.zzheads.trypostgresql.model.MUser;
import com.zzheads.trypostgresql.model.Role;
import com.zzheads.trypostgresql.service.MUserService;
import com.zzheads.trypostgresql.service.RoleService;
import com.zzheads.trypostgresql.utils.HexEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by alexeypapin on 04.09.16.
 */
@Controller
public class MUserApi {
    private final MUserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MUserApi(MUserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody String addUser(@RequestBody String jsonString) {
        HexEncoder encoded = new HexEncoder(jsonString);
        MUser user = MUser.fromJson(encoded.getDecoded());
        Role role = roleService.findByName(user.getRole().getName());
        if (role == null) {
            role = new Role("ROLE_USER");
            roleService.save(role);
        }
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setId(userService.save(user));
        return user.toJson();
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus (HttpStatus.OK)
    public @ResponseBody String getAllUsers() {
        List<MUser> users = userService.findAll();
        return MUser.toJson(users);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT, produces = {"application/json"})
    @ResponseStatus (HttpStatus.OK)
    public @ResponseBody String updateUser(@RequestBody String jsonString, @PathVariable Long id) {
        MUser updatingUser = MUser.fromJson(jsonString);
        Role role = updatingUser.getRole();
        if (roleService.findByName(role.getName()) == null) { // new Role
            roleService.save(role);
        } else {
            role = roleService.findByName(role.getName());
            updatingUser.setRole(role);
        }

        MUser user = userService.findById(id);
        user.setProperties(updatingUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setId(userService.save(user));
        return user.toJson();
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus (HttpStatus.OK)
    public @ResponseBody String getUserById(@PathVariable Long id) {
        MUser user = userService.findById(id);
        return user.toJson();
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE, produces = {"application/json"})
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long id) {
        MUser user = userService.findById(id);
        userService.delete(user);
    }
}
