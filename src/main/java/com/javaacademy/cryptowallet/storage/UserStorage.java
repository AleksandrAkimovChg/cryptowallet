package com.javaacademy.cryptowallet.storage;

import com.javaacademy.cryptowallet.model.user.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserStorage {
    private final Map<String, User> userData = new HashMap<>();

    public void saveUser(User user) {
        userData.put(user.getLogin(), user);
    }

    public Map<String, User> getUserData() {
        return new HashMap<>(userData);
    }
}
