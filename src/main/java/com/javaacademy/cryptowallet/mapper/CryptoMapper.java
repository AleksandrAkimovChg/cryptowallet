package com.javaacademy.cryptowallet.mapper;

import com.javaacademy.cryptowallet.dto.AccountDtoRs;
import com.javaacademy.cryptowallet.dto.UserDtoRq;
import com.javaacademy.cryptowallet.model.account.Account;
import com.javaacademy.cryptowallet.model.user.User;
import org.springframework.stereotype.Service;

@Service
public class CryptoMapper {

    public User convertToUser(UserDtoRq userDto) {
        return new User(userDto.getLogin(), userDto.getEmail(), userDto.getPassword());
    }

    public AccountDtoRs converToAccountDtoRs(Account account) {
        return new AccountDtoRs(account.getCoin(), account.getBalance(), account.getUuid());
    }
}
