package com.javaacademy.cryptowallet.service;

import com.javaacademy.cryptowallet.dto.AccountDtoRs;
import com.javaacademy.cryptowallet.dto.CreateAccountDtoRq;
import com.javaacademy.cryptowallet.mapper.CryptoMapper;
import com.javaacademy.cryptowallet.model.account.Account;
import com.javaacademy.cryptowallet.model.account.CryptoCoin;
import com.javaacademy.cryptowallet.model.user.User;
import com.javaacademy.cryptowallet.repository.AccountRepository;
import com.javaacademy.cryptowallet.service.coin_price_service.CoinPriceService;
import com.javaacademy.cryptowallet.service.course_service.ConvertCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CryptowalletService {
    private final AccountRepository accountRepository;
    private final CryptoMapper mapper;
    private final CoinPriceService coinPriceService;
    private final ConvertCourseService convertCourseService;
    private final UserService userService;
    private static final int SCALE_FOR_DIVIDE = 5;
    private static final String OPERATION_TEMPLATE = "Операция прошла успешно. Продано: %s %s.";

    public Account findAccountUuid(UUID uuid) {
        return accountRepository.getAccountByUuid(uuid).orElseThrow(() -> new RuntimeException("Счет не найден"));
    }

    private List<Account> getAllUserAccounts(User user) {
        String login = user.getLogin();
        return accountRepository.getAccountsByLogin(login);
    }

    public List<AccountDtoRs> findAllAccounts(String login) {
        User user = userService.getUserByLogin(login);
        return getAllUserAccounts(user).stream().map(mapper::converToAccountDtoRs).toList();
    }

    public UUID createCryptoWallet(CreateAccountDtoRq request) {
        User user = userService.getUserByLogin(request.getUsername());
        Account account = new Account(user.getLogin(), request.getCryptoType());
        account.setUuid(UUID.randomUUID());
        accountRepository.saveAccount(account);
        return account.getUuid();
    }

    private Optional<BigDecimal> amountForRubOperation(Account wallet, BigDecimal amountRub) {
        CryptoCoin coin = wallet.getCoin();
        BigDecimal coinPriceInUsd = coinPriceService.getCoinPriceInUsd(coin).orElse(null);
        BigDecimal convertedRubToUsd = convertCourseService.convertRubToUsd(amountRub).orElse(null);
        if (coinPriceInUsd != null && convertedRubToUsd != null) {
            return Optional.of(convertedRubToUsd.divide(coinPriceInUsd, SCALE_FOR_DIVIDE, RoundingMode.HALF_UP));
        }
        return Optional.empty();
    }

    public void refill(UUID uuid, BigDecimal amountRub) {
        Account wallet = findAccountUuid(uuid);
        BigDecimal refillAmount = amountForRubOperation(wallet, amountRub).orElseThrow();
        wallet.setBalance(wallet.getBalance().add(refillAmount));
    }

    public CryptoMessage withdrawal(UUID uuid, BigDecimal amountRub) {
        Account wallet = findAccountUuid(uuid);
        BigDecimal withdrawalAmount = amountForRubOperation(wallet, amountRub).orElse(null);
        if (withdrawalAmount != null && wallet.getBalance().compareTo(withdrawalAmount) < 0) {
            throw new RuntimeException("Нет столько криптовалюты");
        }
        wallet.setBalance(wallet.getBalance().subtract(withdrawalAmount));
        return CryptoMessage.builder()
                .isSuccessful(true)
                .text(OPERATION_TEMPLATE.formatted(withdrawalAmount, wallet.getCoin().name()))
                .build();
    }

    public Optional<BigDecimal> getBalanceInRub(UUID uuid) {
        Account wallet = findAccountUuid(uuid);
        CryptoCoin coin = wallet.getCoin();
        BigDecimal balance = wallet.getBalance();
        BigDecimal coinPriceInUsd = coinPriceService.getCoinPriceInUsd(coin).orElse(null);
        if (coinPriceInUsd != null) {
            BigDecimal balanceInUsd = balance.multiply(coinPriceInUsd);
            return convertCourseService.convertUsdToRub(balanceInUsd);
        }
        return Optional.empty();
    }

    public BigDecimal getAllCryptoWalletBalanceInRub(String login) {
        User user = userService.getUserByLogin(login);
        return getAllUserAccounts(user).stream().map(e -> getBalanceInRub(e.getUuid()).orElse(null))
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
