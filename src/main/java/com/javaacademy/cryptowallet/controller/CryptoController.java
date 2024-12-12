package com.javaacademy.cryptowallet.controller;

import com.javaacademy.cryptowallet.dto.CreateAccountDtoReq;
import com.javaacademy.cryptowallet.model.Account;
import com.javaacademy.cryptowallet.model.CryptoCoin;
import com.javaacademy.cryptowallet.service.CryptoService;
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
    private final CryptoService cryptoService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UUID createAccount(@RequestBody() CreateAccountDtoReq createAccountDtoReq) {
        log.info("userDto input: {}", createAccountDtoReq);
        CryptoCoin coin;
        try {
            coin = CryptoCoin.valueOf(createAccountDtoReq.getCryptoType());
        } catch (Exception e) {
            log.error("Передана не поддерживаемая криптовалюта", e);
        }
        return cryptoService.createCryptoWallet(createAccountDtoReq);
    }

    @GetMapping
    public List<Account> getAccounts(@RequestParam String username) {
        log.info("username input: {}", username);
        return cryptoService.findAllAccounts(username);
    }
}
