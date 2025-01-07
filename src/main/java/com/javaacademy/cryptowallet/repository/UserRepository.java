package com.javaacademy.cryptowallet.repository;

import com.javaacademy.cryptowallet.exception.UserWithLoginAlreadyExistsException;
import com.javaacademy.cryptowallet.model.user.User;
import com.javaacademy.cryptowallet.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepository {
    public static final String USER_ALREADY_EXIST = "Пользователь с таким логином уже существует";
    private final UserStorage userStorage;

    public Optional<User> getUser(String userLogin) {
        return Optional.ofNullable(userStorage.getUserData().get(userLogin));
    }

    public void saveUser(User user) {
        if (getUser(user.getLogin()).isPresent()) {
            throw new UserWithLoginAlreadyExistsException(USER_ALREADY_EXIST);
        }
        userStorage.saveUser(user);
    }
}
