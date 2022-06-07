package com.wcc.geographicdistance.service;

import com.wcc.geographicdistance.domain.Role;
import com.wcc.geographicdistance.domain.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();
}
