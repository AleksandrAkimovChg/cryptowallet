package com.javaacademy.cryptowallet.service;

import com.javaacademy.cryptowallet.dto.AccountDtoRs;
import com.javaacademy.cryptowallet.exception.AccountNotFoundException;
import com.javaacademy.cryptowallet.exception.LowBalanceException;
import com.javaacademy.cryptowallet.mapper.CryptoMapper;
import com.javaacademy.cryptowallet.model.account.Account;
import com.javaacademy.cryptowallet.model.account.CryptoCoinType;
import com.javaacademy.cryptowallet.model.user.User;
import com.javaacademy.cryptowallet.repository.AccountRepository;
import com.javaacademy.cryptowallet.service.converter.ConvertCourseService;
import com.javaacademy.cryptowallet.service.coin_price.CoinPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoWalletService {
    public static final String ACCOUNT_NOT_FOUND = "Счет не найден";
    public static final String OPERATION_TEMPLATE = "Операция прошла успешно. Продано: %s %s.";
    public static final String LOW_BALANCE = "Нет столько криптовалюты";
    private final AccountRepository accountRepository;
    private final CryptoMapper mapper;
    private final CoinPriceService coinPriceService;
    private final ConvertCourseService convertCourseService;
    private final UserService userService;


    public Account findAccountByUuid(UUID uuid) {
        return accountRepository.getAccountByUuid(uuid)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND));
    }

    public List<AccountDtoRs> findAllAccounts(String login) {
        User user = userService.getUserByLogin(login);
        return findAllAccounts(user).stream().map(mapper::converToAccountDtoRs).toList();
    }

    private List<Account> findAllAccounts(User user) {
        String login = user.getLogin();
        return accountRepository.getAllAccountsByLogin(login);
    }

    public UUID createCryptoWallet(String login, CryptoCoinType cryptoCoinType) {
        User user = userService.getUserByLogin(login);
        Account account = new Account(user.getLogin(), cryptoCoinType);
        account.setUuid(UUID.randomUUID());
        accountRepository.saveAccount(account);
        return account.getUuid();
    }

    private BigDecimal getCoinAmountForOperation(Account wallet, BigDecimal amountRub) {
        CryptoCoinType coin = wallet.getCoin();
        BigDecimal coinPriceInUsd = coinPriceService.getCoinPriceInUsd(coin);
        BigDecimal convertedRubToUsd = convertCourseService.convertRubToUsd(amountRub);
        BigDecimal coinAmount = convertedRubToUsd.divide(coinPriceInUsd, coin.getDecimalScale(), RoundingMode.HALF_UP);
        log.info("Сумма операции в криптовалюте: {} {}", coinAmount, coin);
        return coinAmount;
    }

    public void refill(UUID uuid, BigDecimal amountRub) {
        Account wallet = findAccountByUuid(uuid);
        log.info("Кошелек до операции: {}", wallet);
        BigDecimal refillAmount = getCoinAmountForOperation(wallet, amountRub);
        wallet.setBalance(wallet.getBalance().add(refillAmount));
        log.info("Кошелек после операции: {}", wallet);
    }

    public String withdrawal(UUID uuid, BigDecimal amountRub) {
        Account wallet = findAccountByUuid(uuid);
        log.info("Кошелек до операции: {}", wallet);
        BigDecimal withdrawalAmount = getCoinAmountForOperation(wallet, amountRub);
        checkBalance(wallet, withdrawalAmount);
        wallet.setBalance(wallet.getBalance().subtract(withdrawalAmount));
        log.info("Кошелек после операции: {}", wallet);
        return OPERATION_TEMPLATE.formatted(withdrawalAmount.toPlainString(), wallet.getCoin().name());
    }

    private void checkBalance(Account wallet, BigDecimal withdrawalAmount) {
        if (wallet.getBalance().compareTo(withdrawalAmount) < 0) {
            throw new LowBalanceException(LOW_BALANCE);
        }
    }

    public BigDecimal getBalanceInRub(UUID uuid) {
        Account wallet = findAccountByUuid(uuid);
        return getBalanceInRub(wallet);
    }

    private BigDecimal getBalanceInRub(Account account) {
        log.info("Кошелек до операции: {}", account);
        BigDecimal balanceInUsd = getBalanceInUsd(account);
        BigDecimal balanceInRub = convertCourseService.convertUsdToRub(balanceInUsd);
        return convertCourseService.convertUsdToRub(balanceInUsd);
    }

    private BigDecimal getBalanceInUsd(Account wallet) {
        BigDecimal coinPriceInUsd = coinPriceService.getCoinPriceInUsd(wallet.getCoin());
        BigDecimal balanceInUsd = wallet.getBalance().multiply(coinPriceInUsd);
        log.info("Стоимость криптовалюты в долларах: {}", balanceInUsd);
        return balanceInUsd;
    }

    public BigDecimal getAllCryptoWalletBalanceInRub(String login) {
        User user = userService.getUserByLogin(login);
        return findAllAccounts(user).stream().map(this::getBalanceInRub).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
