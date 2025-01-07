package com.javaacademy.cryptowallet.service;

import com.javaacademy.cryptowallet.dto.ResetPasswordDtoRq;
import com.javaacademy.cryptowallet.dto.UserDtoRq;
import com.javaacademy.cryptowallet.exception.PasswordDoesNotMatchException;
import com.javaacademy.cryptowallet.exception.UserNotFoundException;
import com.javaacademy.cryptowallet.mapper.CryptoMapper;
import com.javaacademy.cryptowallet.model.user.User;
import com.javaacademy.cryptowallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    public static final String PASSWORD_NOT_EQUALS = "Пароль не совпадает";
    public static final String USER_NOT_FOUND = "Юзер не найден";
    private final UserRepository userRepository;
    private final CryptoMapper cryptoMapper;

    public void saveUser(UserDtoRq userDto) {
        userRepository.saveUser(cryptoMapper.convertToUser(userDto));
    }

    public User getUserByLogin(String userLogin) {
        return userRepository.getUser(userLogin).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    public void changePassword(ResetPasswordDtoRq resetPasswordDto) {
        User user = getUserByLogin(resetPasswordDto.getLogin());
        checkPassword(resetPasswordDto, user);
        user.setPassword(resetPasswordDto.getNewPassword());
    }

    private void checkPassword(ResetPasswordDtoRq resetPasswordDto, User user) {
        if (!Objects.equals(resetPasswordDto.getOldPassword(), user.getPassword())) {
            throw new PasswordDoesNotMatchException(PASSWORD_NOT_EQUALS);
        }
    }
}
