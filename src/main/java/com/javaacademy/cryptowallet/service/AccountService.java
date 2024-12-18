package com.javaacademy.cryptowallet.service;

import com.javaacademy.cryptowallet.dto.AccountDtoRs;
import com.javaacademy.cryptowallet.dto.CreateAccountDtoRq;
import com.javaacademy.cryptowallet.mapper.CryptoMapper;
import com.javaacademy.cryptowallet.model.account.Account;
import com.javaacademy.cryptowallet.model.account.CryptoCoin;
import com.javaacademy.cryptowallet.model.user.User;
import com.javaacademy.cryptowallet.repository.AccountRepository;
import com.javaacademy.cryptowallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CryptoMapper mapper;


    public Account findUuid(UUID uuid) {
        return accountRepository.getAccountByUuid(uuid).orElseThrow(() -> new RuntimeException("Счет не найден"));
    }

    public List<AccountDtoRs> findAllAccounts(String login) {
        User user = userRepository.getUser(login).orElseThrow(() -> new RuntimeException("Юзер не найден"));
        return accountRepository.getAccountsByLogin(user.getLogin()).stream()
                .map(mapper::converToAccountDtoRs).toList();
    }

    public UUID createCryptoWallet(CreateAccountDtoRq request) {
        User user = userRepository.getUser(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Юзер не найден"));
        Account account = new Account(user.getLogin(), CryptoCoin.valueOf(request.getCryptoType()));
        account.setUuid(UUID.randomUUID());
        accountRepository.saveAccount(account);
        return account.getUuid();
    }
}
