package com.javaacademy.cryptowallet.controller;

import com.javaacademy.cryptowallet.dto.AccountDtoRs;
import com.javaacademy.cryptowallet.dto.CreateAccountDtoRq;
import com.javaacademy.cryptowallet.dto.CryptoWalletDto;
import com.javaacademy.cryptowallet.service.CryptoMessage;
import com.javaacademy.cryptowallet.service.CryptowalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/cryptowallet")
public class CryptowalletController {
    private final CryptowalletService cryptoService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UUID createAccount(@RequestBody CreateAccountDtoRq createAccountDtoRq) {
        log.info("userDto input: {}", createAccountDtoRq);
        return cryptoService.createCryptoWallet(createAccountDtoRq);
    }

    @GetMapping
    public List<AccountDtoRs> getAccounts(@RequestParam String username) {
        log.info("username input: {}", username);
        return cryptoService.findAllAccounts(username);
    }

    @PostMapping("/refill")
    private void refill(@RequestBody CryptoWalletDto cryptoWalletDto) {
        log.info("refill input: {}", cryptoWalletDto);
        cryptoService.refill(cryptoWalletDto.getUuid(), cryptoWalletDto.getAmountRub());
    }

    @PostMapping("/withdrawal")
    private CryptoMessage withdrawal(@RequestBody CryptoWalletDto cryptoWalletDto) {
        log.info("withdrawal input: {}", cryptoWalletDto);
        return cryptoService.withdrawal(cryptoWalletDto.getUuid(), cryptoWalletDto.getAmountRub());
    }

    @GetMapping("/balance/{id}")
    public BigDecimal getAccountBalanceInRub(@PathVariable UUID id) {
        log.info("getBalanceInRub input: {}", id);
        return cryptoService.getBalanceInRub(id).orElseThrow();
    }

    @GetMapping("/balance")
    public BigDecimal getAllAccountsBalanceInRub(@RequestParam String username) {
        log.info("getAllAccountsBalanceInRub input: {}", username);
        return cryptoService.getAllCryptoWalletBalanceInRub(username);
    }
}
