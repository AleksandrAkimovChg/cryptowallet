package com.javaacademy.cryptowallet.storage;

import com.javaacademy.cryptowallet.model.account.Account;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AccountStorage {
    private final Map<UUID, Account> accountData = new HashMap<>();

    public void saveAccount(Account account) {
        accountData.put(account.getUuid(), account);
    }

    public Account getAccountData(UUID uuid) {
        return accountData.get(uuid);
    }

    public Map<UUID, Account> getAccountData() {
        return new HashMap<>(accountData);
    }
}
