package com.javaacademy.cryptowallet.mapper;

import com.javaacademy.cryptowallet.dto.UserDtoReq;
import com.javaacademy.cryptowallet.model.User;
import org.springframework.stereotype.Service;

@Service
public class CryptoMapper {

    public User convertToUser(UserDtoReq userDto) {
        return new User(userDto.getLogin(), userDto.getEmail(), userDto.getPassword());
    }
}
