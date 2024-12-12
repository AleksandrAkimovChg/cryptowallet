package com.javaacademy.cryptowallet.service;

import com.javaacademy.cryptowallet.dto.ResetPasswordDtoReq;
import com.javaacademy.cryptowallet.dto.UserDtoReq;
import com.javaacademy.cryptowallet.mapper.CryptoMapper;
import com.javaacademy.cryptowallet.model.User;
import com.javaacademy.cryptowallet.repository.CryptoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final CryptoRepository repository;
    private final CryptoMapper mapper;

    public void saveUser(UserDtoReq userDto) {
        repository.saveUser(mapper.convertToUser(userDto));
    }

    private User getUserByLogin(String userLogin) {
        return repository.getUser(userLogin).orElseThrow();
    }

    public void changePassword(ResetPasswordDtoReq resetPasswordDto) {
        User user = getUserByLogin(resetPasswordDto.getLogin());
        if (!Objects.equals(resetPasswordDto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Пароль не совпадает");
        }
        user.setPassword(resetPasswordDto.getNewPassword());
    }
}
