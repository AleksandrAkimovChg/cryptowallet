package com.javaacademy.cryptowallet.controller;

import com.javaacademy.cryptowallet.dto.AccountDtoRs;
import com.javaacademy.cryptowallet.dto.CreateAccountDtoRq;
import com.javaacademy.cryptowallet.model.account.CryptoCoin;
import com.javaacademy.cryptowallet.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/cryptowallet")
public class CryptoController {
    private final AccountService cryptoService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UUID createAccount(@RequestBody() CreateAccountDtoRq createAccountDtoRq) {
        log.info("userDto input: {}", createAccountDtoRq);
        checkCryptoCoin(createAccountDtoRq.getCryptoType());
        return cryptoService.createCryptoWallet(createAccountDtoRq);
    }

    @GetMapping
    public List<AccountDtoRs> getAccounts(@RequestParam String username) {
        log.info("username input: {}", username);
        return cryptoService.findAllAccounts(username);
    }

    private void checkCryptoCoin(String cryptoType) {
        try {
            CryptoCoin.valueOf(cryptoType);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Передана не поддерживаемая криптовалюта: " + cryptoType);
        }
    }
}