package com.javaacademy.cryptowallet.repository;

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
    private final AccountStorage accountStorage;

    public void saveAccount(Account account) {
        accountStorage.saveAccount(account);
    }
    public Optional<Account> getAccountByUuid(UUID uuid) {
        return Optional.ofNullable(accountStorage.getAccountData().get(uuid));
    }

    public List<Account> getAccountsByLogin(String login) {
        return accountStorage.getAccountData().values().stream()
                .filter(e -> Objects.equals(login, e.getLogin())).toList();
    }
}
