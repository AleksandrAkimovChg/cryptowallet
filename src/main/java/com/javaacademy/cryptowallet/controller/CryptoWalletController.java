package com.javaacademy.cryptowallet.controller;

import com.javaacademy.cryptowallet.dto.AccountDtoRs;
import com.javaacademy.cryptowallet.dto.CreateAccountDtoRq;
import com.javaacademy.cryptowallet.dto.CryptoWalletDtoRq;
import com.javaacademy.cryptowallet.exception.AccountNotFoundException;
import com.javaacademy.cryptowallet.exception.CoinUnsupportedException;
import com.javaacademy.cryptowallet.exception.LowBalanceException;
import com.javaacademy.cryptowallet.exception.UserNotFoundException;
import com.javaacademy.cryptowallet.model.account.CryptoCoinType;
import com.javaacademy.cryptowallet.service.CryptoWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Tag(name = "Crypto wallet controller",
        description = "API для управления созданием пользователя или для смены пароля существующего пользователя")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/cryptowallet")
public class CryptoWalletController {
    public static final String BALANCE_UNAVAILABLE = "Баланс временно не доступно. Попробуйте позднее";
    public static final String WITHDRAWAL_UNAVAILABLE = "Снятие временно не доступно. Попробуйте позднее";
    public static final String REFILL_UNAVAILABLE = "Пополнение временно не доступно. Попробуйте позднее";
    public static final String CRYPTO_COIN_NOT_ACCEPTED = "Передан неподдерживаемый тип валюты. Доступны значения: %s";
    public static final String NO_ACCOUNTS = "Нет счетов";
    public static final String REFILL_SUCCESS = "Пополнение успешно";
    private final CryptoWalletService cryptoService;

    @Operation(summary = "Создание криптовалютного кошелька в системе",
            description = "Ресурс позволяет создать криптовалютный кошелек пользователя системы, "
                    + "с одним из поддерживаемых видов криптовалюты")
    @ApiResponse(
            responseCode = "201",
            description = "Успешное создание кошелька",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = UUID.class))
            }
    )
    @ApiResponse(
            responseCode = "400",
            description = "Неуспешное создание криптовалютного кошелька (Выбран неподдерживаемый вид валюты)",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @ApiResponse(
            responseCode = "404",
            description = "Неуспешное создание криптовалютного кошелька (Пользователь с таким логином отсутствует)",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountDtoRq createAccountDtoRq) {
        try {
            CryptoCoinType cryptoCoinType = checkCryptoCoinType(createAccountDtoRq);
            UUID uuid = cryptoService.createCryptoWallet(createAccountDtoRq.getUsername(), cryptoCoinType);
            return ResponseEntity.status(HttpStatus.CREATED).body(uuid);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @Operation(summary = "Получение информации о криптовалютных кошельках",
            description = "Ресурс позволяет получить информацию о всех криптовалютных кошельках пользователя")
    @ApiResponse(
            responseCode = "200",
            description = "Успешное получение информации о криптовалютных кошельках пользователя",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(name = "Нет счетов", value = "Нет счетов",
                                            summary = "Пример ответа")
                            }
                    ),
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = AccountDtoRs.class)))
            }
    )
    @ApiResponse(
            responseCode = "404",
            description = "Неуспешное получение информации о криптовалютных кошельках пользователя "
                    + "(Пользователь с таким логином отсутствует)",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @GetMapping
    public ResponseEntity<?> getAccounts(
            @Parameter(description = "Логин пользователя", required = true)
            @RequestParam("username") String userName) {
        try {
            List<AccountDtoRs> allAccounts = cryptoService.findAllAccounts(userName);
            if (allAccounts.isEmpty()) {
                return ResponseEntity.ok().body(NO_ACCOUNTS);
            }
            return ResponseEntity.ok(allAccounts);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @Operation(summary = "Пополнение криптовалютного кошелька",
            description = "Пользователь сервиса может пополнить свой криптовалютный кошелек")
    @ApiResponse(
            responseCode = "200",
            description = "Успешное пополнение криптовалютного кошелька",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @ApiResponse(
            responseCode = "404",
            description = "Неуспешное пополнение криптовалютного кошелька (Кошелек не найден)",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @ApiResponse(
            responseCode = "503",
            description = "Сервис временно недоступен",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(name = "Сервис временно недоступен", value = REFILL_UNAVAILABLE,
                                            summary = "Пример ответа")
                            }
                    )
            }
    )
    @PostMapping("/refill")
    public ResponseEntity<String> refill(@RequestBody CryptoWalletDtoRq cryptoWalletDto) {
        try {
            cryptoService.refill(cryptoWalletDto.getUuid(), cryptoWalletDto.getAmountRub());
            return ResponseEntity.ok(REFILL_SUCCESS);
        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(REFILL_UNAVAILABLE);
        }
    }

    @Operation(summary = "Снятие криптовалюты с кошелька",
            description = "Пользователь сервиса может снять криптовалюту с кошелька")
    @ApiResponse(
            responseCode = "200",
            description = "Успешное снятие криптовалюты с кошелька",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @ApiResponse(
            responseCode = "400",
            description = "Неуспешное снятие криптовалюты с кошелька "
                    + "(Недостаточный баланс для операции)",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @ApiResponse(
            responseCode = "404",
            description = "Неуспешное снятие криптовалюты с кошелька (Кошелек не найден)",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @ApiResponse(
            responseCode = "503",
            description = "Сервис временно недоступен",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(name = "Сервис временно недоступен", value = WITHDRAWAL_UNAVAILABLE,
                                            summary = "Пример ответа")
                            }
                    )
            }
    )
    @PostMapping("/withdrawal")
    public ResponseEntity<String> withdrawal(@RequestBody CryptoWalletDtoRq cryptoWalletDto) {
        try {
            return ResponseEntity.ok(
                    cryptoService.withdrawal(cryptoWalletDto.getUuid(), cryptoWalletDto.getAmountRub()));
        } catch (LowBalanceException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(WITHDRAWAL_UNAVAILABLE);
        }
    }

    @Operation(summary = "Получение рублевого баланса криптовалютного кошелька",
            description = "Пользователь сервиса может узнать баланс криптовалютного кошелька в рублях")
    @ApiResponse(
            responseCode = "200",
            description = "Успешное получение рублевого баланса",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @ApiResponse(
            responseCode = "404",
            description = "Неуспешное получение рублевого баланса (Кошелек не найден)",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @ApiResponse(
            responseCode = "503",
            description = "Сервис временно недоступен",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(name = "Сервис временно недоступен", value = BALANCE_UNAVAILABLE,
                                            summary = "Пример ответа")
                            }
                    )
            }
    )
    @GetMapping("/balance/{id}")
    public ResponseEntity<?> getAccountBalanceInRub(
            @Parameter(description = "UUID кошелька", required = true)
            @PathVariable UUID id) {
        try {
            return ResponseEntity.ok(cryptoService.getBalanceInRub(id));
        } catch (AccountNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(BALANCE_UNAVAILABLE);
        }
    }

    @Operation(summary = "Получение рублевого баланса всех криптовалютных кошельков",
            description = "Пользователь сервиса может узнать рублевый баланс его криптовалютных кошельков")
    @ApiResponse(
            responseCode = "200",
            description = "Успешное получение рублевого баланса всех кошельков",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = BigDecimal.class))
            }
    )
    @ApiResponse(
            responseCode = "404",
            description = "Неуспешное получение рублевого баланса  (Пользователь не найден)",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @ApiResponse(
            responseCode = "503",
            description = "Сервис временно недоступен",
            content = {
                    @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(name = "Сервис временно недоступен", value = BALANCE_UNAVAILABLE,
                                            summary = "Пример ответа")
                            }
                    )
            }
    )
    @GetMapping("/balance")
    public ResponseEntity<?> getAllAccountsBalanceInRub(
            @Parameter(description = "Login пользователя", required = true)
            @RequestParam("username") String userName) {
        try {
            return ResponseEntity.ok(cryptoService.getAllCryptoWalletBalanceInRub(userName));
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(BALANCE_UNAVAILABLE);
        }
    }

    private CryptoCoinType checkCryptoCoinType(CreateAccountDtoRq createAccountDtoRq) {
        try {
            return CryptoCoinType.valueOf(createAccountDtoRq.getCryptoType());
        } catch (IllegalArgumentException ex) {
            log.info(ex.getMessage(), ex);
            throw new CoinUnsupportedException(
                    CRYPTO_COIN_NOT_ACCEPTED.formatted(Arrays.toString(CryptoCoinType.values())));
        }
    }
}
