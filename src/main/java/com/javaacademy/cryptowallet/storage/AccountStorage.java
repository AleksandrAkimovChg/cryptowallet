package com.javaacademy.cryptowallet.storage;

import com.javaacademy.cryptowallet.model.Account;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AccountStorage {
    private final Map<UUID, Account> accountData = new HashMap<>();

    public void saveAccount(Account account) {
        if (accountData.containsKey(account.getUuid())) {
            throw new RuntimeException("Такой криптокошелек уже существует");
        }
        accountData.put(account.getUuid(), account);
    }

    public Map<UUID, Account> getAccountData() {
        return new HashMap<>(accountData);
    }
}
