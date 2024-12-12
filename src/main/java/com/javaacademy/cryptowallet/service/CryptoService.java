package com.javaacademy.cryptowallet.service;

import com.javaacademy.cryptowallet.dto.CreateAccountDtoReq;
import com.javaacademy.cryptowallet.model.Account;
import com.javaacademy.cryptowallet.model.CryptoCoin;
import com.javaacademy.cryptowallet.model.User;
import com.javaacademy.cryptowallet.repository.CryptoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("cryptoService")
@RequiredArgsConstructor
public class CryptoService {
    private final CryptoRepository repository;

    public Account findUuid(UUID uuid) {
        return repository.getAccountByUuid(uuid).orElseThrow(() -> new RuntimeException("Счет не найден"));
    }

    public List<Account> findAllAccounts(String login) {
        User user = repository.getUser(login).orElseThrow(() -> new RuntimeException("Юзер не найден"));
        return repository.getAccountsByLogin(user.getLogin());
    }

    public UUID createCryptoWallet(CreateAccountDtoReq request) {
        User user = repository.getUser(request.getUsername()).orElseThrow(() -> new RuntimeException("Юзер не найден"));
        Account account = new Account(user.getLogin(), CryptoCoin.valueOf(request.getCryptoType()));
        repository.saveAccount(account);
        return account.getUuid();
    }
}
