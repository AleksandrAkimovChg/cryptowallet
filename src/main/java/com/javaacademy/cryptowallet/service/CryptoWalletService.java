package com.javaacademy.cryptowallet.service;

import com.javaacademy.cryptowallet.dto.AccountDtoRs;
import com.javaacademy.cryptowallet.dto.CreateAccountDtoRq;
import com.javaacademy.cryptowallet.exception.AccountNotFoundException;
import com.javaacademy.cryptowallet.exception.CoinUnsupportedException;
import com.javaacademy.cryptowallet.exception.LowBalanceException;
import com.javaacademy.cryptowallet.mapper.CryptoMapper;
import com.javaacademy.cryptowallet.model.account.Account;
import com.javaacademy.cryptowallet.model.account.CryptoCoinType;
import com.javaacademy.cryptowallet.model.user.User;
import com.javaacademy.cryptowallet.repository.AccountRepository;
import com.javaacademy.cryptowallet.service.converter.ConvertCourseService;
import com.javaacademy.cryptowallet.service.integration.coin_price.CoinPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoWalletService {
    private final AccountRepository accountRepository;
    private final CryptoMapper mapper;
    private final CoinPriceService coinPriceService;
    private final ConvertCourseService convertCourseService;
    private final UserService userService;
    private static final int SCALE_FOR_DIVIDE = 8;
    private static final String OPERATION_TEMPLATE = "Операция прошла успешно. Продано: %s %s.";

    public Account findAccountByUuid(UUID uuid) {
        return accountRepository.getAccountByUuid(uuid)
                .orElseThrow(() -> new AccountNotFoundException("Счет не найден"));
    }

    public List<AccountDtoRs> findAllAccounts(String login) {
        User user = userService.getUserByLogin(login);
        return findAllAccounts(user).stream().map(mapper::converToAccountDtoRs).toList();
    }

    private List<Account> findAllAccounts(User user) {
        String login = user.getLogin();
        return accountRepository.getAllAccountsByLogin(login);
    }

    public CryptoCoinType checkCryptoCoinType(CreateAccountDtoRq createAccountDtoRq) {
        try {
            return CryptoCoinType.valueOf(createAccountDtoRq.getCryptoType());
        } catch (IllegalArgumentException ex) {
            log.info(ex.getMessage(), ex);
            throw new CoinUnsupportedException("Передан неподдерживаемый тип валюты. Доступны:"
                    + Arrays.toString(CryptoCoinType.values()));
        }
    }

    public UUID createCryptoWallet(String login, CryptoCoinType cryptoCoinType) {
        User user = userService.getUserByLogin(login);
        Account account = new Account(user.getLogin(), cryptoCoinType);
        account.setUuid(UUID.randomUUID());
        accountRepository.saveAccount(account);
        return account.getUuid();
    }

    private BigDecimal amountForOperation(Account wallet, BigDecimal amountRub) {
        CryptoCoinType coin = wallet.getCoin();
        BigDecimal coinPriceInUsd = coinPriceService.getCoinPriceInUsd(coin);
        BigDecimal convertedRubToUsd = convertCourseService.convertRubToUsd(amountRub);
        BigDecimal result = convertedRubToUsd.divide(coinPriceInUsd, coin.getDivideScale(), RoundingMode.HALF_UP);
        log.info("Сумма операции: {}", result);
        return result;
    }

    public void refill(UUID uuid, BigDecimal amountRub) {
        Account wallet = findAccountByUuid(uuid);
        log.info("Кошелек до операции: {}", wallet);
        BigDecimal refillAmount = amountForOperation(wallet, amountRub);
        wallet.setBalance(wallet.getBalance().add(refillAmount));
        log.info("Кошелек после операции: {}", wallet);
    }

    public String withdrawal(UUID uuid, BigDecimal amountRub) {
        Account wallet = findAccountByUuid(uuid);
        log.info("Кошелек до операции: {}", wallet);
        BigDecimal withdrawalAmount = amountForOperation(wallet, amountRub);
        if (wallet.getBalance().compareTo(withdrawalAmount) < 0) {
            throw new LowBalanceException("Нет столько криптовалюты");
        }
        wallet.setBalance(wallet.getBalance().subtract(withdrawalAmount));
        log.info("Кошелек после операции: {}", wallet);
        return OPERATION_TEMPLATE.formatted(withdrawalAmount, wallet.getCoin().name());
    }

    public BigDecimal getBalanceInRub(UUID uuid) {
        Account wallet = findAccountByUuid(uuid);
        log.info("Кошелек до операции: {}", wallet);
        CryptoCoinType coin = wallet.getCoin();
        BigDecimal balance = wallet.getBalance();
        BigDecimal coinPriceInUsd = coinPriceService.getCoinPriceInUsd(coin);
        BigDecimal balanceInUsd = balance.multiply(coinPriceInUsd);
        return convertCourseService.convertUsdToRub(balanceInUsd);
    }

    public BigDecimal getAllCryptoWalletBalanceInRub(String login) {
        User user = userService.getUserByLogin(login);
        return findAllAccounts(user).stream()
                .map(e -> getBalanceInRub(e.getUuid())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
