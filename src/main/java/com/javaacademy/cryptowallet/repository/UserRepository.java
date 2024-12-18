package com.javaacademy.cryptowallet.repository;

import com.javaacademy.cryptowallet.model.user.User;
import com.javaacademy.cryptowallet.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepository {
    private final UserStorage userStorage;

    public void saveUser(User user) {
        userStorage.saveUser(user);
    }
    public Optional<User> getUser(String userLogin) {
        return Optional.ofNullable(userStorage.getUserData().get(userLogin));
    }
}
