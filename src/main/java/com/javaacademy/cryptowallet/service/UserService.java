package com.javaacademy.cryptowallet.service;

import com.javaacademy.cryptowallet.dto.ResetPasswordDtoRq;
import com.javaacademy.cryptowallet.dto.UserDtoRq;
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
    private final UserRepository repository;
    private final CryptoMapper mapper;

    public void saveUser(UserDtoRq userDto) {
        repository.saveUser(mapper.convertToUser(userDto));
    }

    private User getUserByLogin(String userLogin) {
        return repository.getUser(userLogin).orElseThrow();
    }

    public void changePassword(ResetPasswordDtoRq resetPasswordDto) {
        User user = getUserByLogin(resetPasswordDto.getLogin());
        if (!Objects.equals(resetPasswordDto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Пароль не совпадает");
        }
        user.setPassword(resetPasswordDto.getNewPassword());
    }
}