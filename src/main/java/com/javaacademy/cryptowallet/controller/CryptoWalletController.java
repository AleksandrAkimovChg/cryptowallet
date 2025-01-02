package com.javaacademy.cryptowallet.controller;

import com.javaacademy.cryptowallet.dto.AccountDtoRs;
import com.javaacademy.cryptowallet.dto.CreateAccountDtoRq;
import com.javaacademy.cryptowallet.dto.CryptoWalletDto;
import com.javaacademy.cryptowallet.model.account.CryptoCoinType;
import com.javaacademy.cryptowallet.service.CryptoMessage;
import com.javaacademy.cryptowallet.service.CryptoWalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cryptowallet")
public class CryptoWalletController {
    private final CryptoWalletService cryptoService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountDtoRq createAccountDtoRq) {
        try {
            CryptoCoinType cryptoCoinType = cryptoService.checkCryptoCoinType(createAccountDtoRq);
            UUID uuid = cryptoService.createCryptoWallet(createAccountDtoRq.getUsername(), cryptoCoinType);
            return ResponseEntity.status(HttpStatus.CREATED).body(uuid);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping
    public List<AccountDtoRs> getAccounts(@RequestParam String username) {
        return cryptoService.findAllAccounts(username);
    }

    @PostMapping("/refill")
    private void refill(@RequestBody CryptoWalletDto cryptoWalletDto) {
        cryptoService.refill(cryptoWalletDto.getUuid(), cryptoWalletDto.getAmountRub());
    }

    @PostMapping("/withdrawal")
    private CryptoMessage withdrawal(@RequestBody CryptoWalletDto cryptoWalletDto) {
        return cryptoService.withdrawal(cryptoWalletDto.getUuid(), cryptoWalletDto.getAmountRub());
    }

    @GetMapping("/balance/{id}")
    public BigDecimal getAccountBalanceInRub(@PathVariable UUID id) {
        return cryptoService.getBalanceInRub(id).orElseThrow();
    }

    @GetMapping("/balance")
    public BigDecimal getAllAccountsBalanceInRub(@RequestParam String username) {
        return cryptoService.getAllCryptoWalletBalanceInRub(username);
    }
}
