package com.javaacademy.cryptowallet.repository;

import com.javaacademy.cryptowallet.exception.AccountAlreadyExistException;
import com.javaacademy.cryptowallet.model.account.Account;
import com.javaacademy.cryptowallet.storage.AccountStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountRepository {
    public static final String ACCOUNT_ALREADY_EXIST = "Криптовалютный кошелек с таким uuid уже существует";
    private final AccountStorage accountStorage;

    public void saveAccount(Account account) {
        if (getAccountByUuid(account.getUuid()).isPresent()) {
            throw new AccountAlreadyExistException(ACCOUNT_ALREADY_EXIST);
        }
        accountStorage.saveAccount(account);
    }
    public Optional<Account> getAccountByUuid(UUID uuid) {
        return Optional.ofNullable(accountStorage.getAccountData(uuid));
    }

    public List<Account> getAllAccountsByLogin(String login) {
        return accountStorage.getAccountData().values().stream()
                .filter(account -> Objects.equals(login, account.getLogin())).toList();
    }
}
