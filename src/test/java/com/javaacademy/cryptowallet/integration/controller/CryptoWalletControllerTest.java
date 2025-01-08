package com.javaacademy.cryptowallet.integration.controller;

import com.javaacademy.cryptowallet.dto.AccountDtoRs;
import com.javaacademy.cryptowallet.dto.CreateAccountDtoRq;
import com.javaacademy.cryptowallet.dto.CryptoWalletDtoRq;
import com.javaacademy.cryptowallet.dto.UserDtoRq;
import com.javaacademy.cryptowallet.exception.UserNotFoundException;
import com.javaacademy.cryptowallet.model.account.Account;
import com.javaacademy.cryptowallet.model.account.CryptoCoinType;
import com.javaacademy.cryptowallet.model.user.User;
import com.javaacademy.cryptowallet.repository.AccountRepository;
import com.javaacademy.cryptowallet.repository.UserRepository;
import com.javaacademy.cryptowallet.service.CryptoWalletService;
import com.javaacademy.cryptowallet.service.UserService;
import com.javaacademy.cryptowallet.service.coin_price.CoinPriceService;
import com.javaacademy.cryptowallet.service.converter.ConvertCourseService;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.javaacademy.cryptowallet.controller.CryptoWalletController.CRYPTO_COIN_NOT_ACCEPTED;
import static com.javaacademy.cryptowallet.controller.CryptoWalletController.NO_ACCOUNTS;
import static com.javaacademy.cryptowallet.controller.CryptoWalletController.REFILL_SUCCESS;
import static com.javaacademy.cryptowallet.integration.controller.util.UtilTestData.createCreateAccountDtoRqWithBtc;
import static com.javaacademy.cryptowallet.integration.controller.util.UtilTestData.createUserDtoRq;
import static com.javaacademy.cryptowallet.service.CryptoWalletService.ACCOUNT_NOT_FOUND;
import static com.javaacademy.cryptowallet.service.CryptoWalletService.LOW_BALANCE;
import static com.javaacademy.cryptowallet.service.CryptoWalletService.OPERATION_TEMPLATE;
import static com.javaacademy.cryptowallet.service.UserService.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("local")
@DisplayName("Тестирование контроллера CryptoWalletController")
public class CryptoWalletControllerTest {
    public static final String REQUEST_PARAM_LOGIN = "username";
    public static final String REFILL_PATH = "/refill";
    public static final String WITHDRAWAL_PATH = "/withdrawal";
    public static final String BALANCE_PATH = "/balance";
    public static final String BALANCE_BY_ID_PATH = BALANCE_PATH + "/{id}";
    private final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBasePath("/cryptowallet")
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private final ResponseSpecification responseSpecification = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build();
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CryptoWalletService cryptoWalletService;
    @Autowired
    private ConvertCourseService convertCourseService;
    @Autowired
    private CoinPriceService coinPriceService;

    @Test
    @DisplayName("Успешное создание криптовалютного кошелька")
    public void createAccountSuccess() {
        CreateAccountDtoRq createAccountDtoRq = createCreateAccountDtoRqWithBtc(saveUser(createUserDtoRq()));

        UUID uuid = RestAssured.given(requestSpecification)
                .body(createAccountDtoRq)
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .body()
                .as(UUID.class);

        assertTrue(accountRepository.getAccountByUuid(uuid).isPresent());
        Account account = accountRepository.getAccountByUuid(uuid).get();
        assertEquals(createAccountDtoRq.getUsername(), account.getLogin());
        assertEquals(CryptoCoinType.valueOf(createAccountDtoRq.getCryptoType()), account.getCoin());
        assertEquals(new BigDecimal("0"), account.getBalance());
    }

    @Test
    @DisplayName("Неуспешное создание криптовалютного кошелька - пользователь с таким логином отсутствует")
    public void createAccountFailureUserNotFound() {
        UserDtoRq userDtoRq = createUserDtoRq();
        assertTrue(userRepository.getUser(userDtoRq.getLogin()).isEmpty());
        CreateAccountDtoRq createAccountDtoRq = createCreateAccountDtoRqWithBtc(userDtoRq);

        RestAssured.given(requestSpecification)
                .body(createAccountDtoRq)
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(USER_NOT_FOUND.formatted(createAccountDtoRq.getUsername())));
    }

    @Test
    @DisplayName("Неуспешное создание криптовалютного кошелька - выбран неподдерживаемый вид валюты")
    public void createAccountCryptoCoinNotAccepted() {
        String login = saveUser(createUserDtoRq()).getLogin();
        CreateAccountDtoRq createAccountDtoRq = new CreateAccountDtoRq(login, "DOGE");

        RestAssured.given(requestSpecification)
                .body(createAccountDtoRq)
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(CRYPTO_COIN_NOT_ACCEPTED.formatted(Arrays.toString(CryptoCoinType.values()))));
    }

    @Test
    @DisplayName("Успешное получение информации о криптовалютных кошельках пользователя")
    public void getAccountsSuccess() {
        User user = saveUser(createUserDtoRq());
        List<UUID> uuidListExpected = List.of(
                createCryptoWalletBtc(user).getUuid(),
                createCryptoWalletEth(user).getUuid(),
                createCryptoWalletSol(user).getUuid());
        List<CryptoCoinType> coinTypeListExpected = List.of(CryptoCoinType.values());

        List<AccountDtoRs> allAccounts = RestAssured.given(requestSpecification)
                .queryParam(REQUEST_PARAM_LOGIN, user.getLogin())
                .get()
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
        List<CryptoCoinType> cryptoCoinTypeListActual = allAccounts.stream().map(AccountDtoRs::getCoin).toList();
        List<UUID> uuidListActual = allAccounts.stream().map(AccountDtoRs::getUuid).toList();

        assertEquals(uuidListExpected.size(), allAccounts.size());
        assertTrue(cryptoCoinTypeListActual.containsAll(coinTypeListExpected));
        assertTrue(uuidListActual.containsAll(uuidListExpected));
    }

    @Test
    @DisplayName("Успешное получение информации о криптовалютных кошельках пользователя")
    public void getAccountsSuccessNoAccounts() {
        String login = saveUser(createUserDtoRq()).getLogin();
        assertTrue(cryptoWalletService.findAllAccounts(login).isEmpty());

        RestAssured.given(requestSpecification)
                .queryParam(REQUEST_PARAM_LOGIN, login)
                .get()
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(NO_ACCOUNTS));
    }

    @Test
    @DisplayName("Неуспешное получение информации о криптовалютных кошельках пользователя - пользователь не найден")
    public void getAccountsFailure() {
        String userName = "login";
        assertThrows(UserNotFoundException.class, () -> cryptoWalletService.findAllAccounts(userName));

        RestAssured.given(requestSpecification)
                .queryParam(REQUEST_PARAM_LOGIN, userName)
                .get()
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(USER_NOT_FOUND.formatted(userName)));
    }

    @Test
    @DisplayName("Успешное пополнение криптовалютного кошелька")
    public void refillSuccess() {
        BigDecimal refillAmountInRub = new BigDecimal("1000000");
        Account account = createCryptoWalletBtc(saveUser(createUserDtoRq()));
        CryptoWalletDtoRq cryptoWalletDto = new CryptoWalletDtoRq(account.getUuid(), refillAmountInRub);
        BigDecimal expected = refillAmountInRub.multiply(convertCourseService.getRubToUsdCourse())
                .divide(coinPriceService.getCoinPriceInUsd(account.getCoin()),
                        account.getCoin().getDecimalScale(), RoundingMode.HALF_UP);

        RestAssured.given(requestSpecification)
                .body(cryptoWalletDto)
                .post(REFILL_PATH)
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(REFILL_SUCCESS));
        assertTrue(accountRepository.getAccountByUuid(account.getUuid()).isPresent());
        BigDecimal actual = accountRepository.getAccountByUuid(account.getUuid()).get().getBalance();

        assertTrue(compareBalance(expected, actual));
        assertTrue(compareBalance(BigDecimal.ONE, actual));
    }

    @Test
    @DisplayName("Неуспешное пополнение криптовалютного кошелька - кошелек не найден")
    public void refillFailure() {
        CryptoWalletDtoRq cryptoWalletDto = new CryptoWalletDtoRq(UUID.randomUUID(), new BigDecimal("100"));

        RestAssured.given(requestSpecification)
                .body(cryptoWalletDto)
                .post(REFILL_PATH)
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(ACCOUNT_NOT_FOUND));
    }

    @Test
    @DisplayName("Успешное снятие c криптовалютного кошелька")
    public void withdrawalSuccess() {
        Account account = createCryptoWalletBtc(saveUser(createUserDtoRq()));
        BigDecimal balanceAmount = BigDecimal.ONE
                .setScale(account.getCoin().getDecimalScale(), RoundingMode.HALF_UP);
        account.setBalance(balanceAmount);
        BigDecimal withdrawalAmountInRub = new BigDecimal("1000000");
        CryptoWalletDtoRq cryptoWalletDto = new CryptoWalletDtoRq(account.getUuid(), withdrawalAmountInRub);
        String message = OPERATION_TEMPLATE.formatted(balanceAmount, account.getCoin().name());

        RestAssured.given(requestSpecification)
                .body(cryptoWalletDto)
                .post(WITHDRAWAL_PATH)
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(message));
        BigDecimal actual = accountRepository.getAccountByUuid(account.getUuid()).get().getBalance();

        assertTrue(compareBalance(BigDecimal.ZERO, actual));
    }

    @Test
    @DisplayName("Неуспешное снятие криптовалютного кошелька - нет столько криптовалюты")
    public void withdrawalFailureLowBalance() {
        Account wallet = createCryptoWalletSol(saveUser(createUserDtoRq()));
        assertTrue(compareBalance(BigDecimal.ZERO, wallet.getBalance()));
        CryptoWalletDtoRq cryptoWalletDto = new CryptoWalletDtoRq(wallet.getUuid(), BigDecimal.ONE);

        RestAssured.given(requestSpecification)
                .body(cryptoWalletDto)
                .post(WITHDRAWAL_PATH)
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(LOW_BALANCE));
    }

    @Test
    @DisplayName("Неуспешное снятие криптовалютного кошелька - счет не найден")
    public void withdrawalFailureAccountNoyFound() {
        CryptoWalletDtoRq cryptoWalletDto = new CryptoWalletDtoRq(UUID.randomUUID(), BigDecimal.ONE);

        RestAssured.given(requestSpecification)
                .body(cryptoWalletDto)
                .post(WITHDRAWAL_PATH)
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(ACCOUNT_NOT_FOUND));
    }

    @Test
    @DisplayName("Успешное получение баланса в рублях криптовалютного кошелька ")
    public void getAccountBalanceInRubSuccess() {
        Account account = createCryptoWalletBtc(saveUser(createUserDtoRq()));
        account.setBalance(BigDecimal.ONE);

        BigDecimal actual = RestAssured.given(requestSpecification)
                .get(BALANCE_BY_ID_PATH, account.getUuid())
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(BigDecimal.class);

        assertTrue(compareBalance(new BigDecimal("1000000"), actual));
    }

    @Test
    @DisplayName("Неуспешное получение баланса в рублях криптовалютного кошелька - кошелек не найден")
    public void getAccountBalanceInRubFailure() {
        RestAssured.given(requestSpecification)
                .get(BALANCE_BY_ID_PATH, UUID.randomUUID())
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(ACCOUNT_NOT_FOUND));
    }

    @Test
    @DisplayName("Успешное получение баланса в рублях всех криптовалютных кошельков")
    public void getAllAccountsBalanceInRubSuccess() {
        User user = saveUser(createUserDtoRq());
        Account accountBtc = createCryptoWalletBtc(user);
        accountBtc.setBalance(BigDecimal.ONE);
        Account accountEth = createCryptoWalletEth(user);
        accountEth.setBalance(BigDecimal.ONE);
        Account accountSol = createCryptoWalletSol(user);
        accountSol.setBalance(BigDecimal.ONE);

        BigDecimal actual = RestAssured.given(requestSpecification)
                .queryParam(REQUEST_PARAM_LOGIN, user.getLogin())
                .get(BALANCE_PATH)
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .as(BigDecimal.class);

        assertTrue(compareBalance(new BigDecimal("3000000"), actual));
    }

    @Test
    @DisplayName("Неуспешное получение баланса в рублях криптовалютного кошелька - пользователь не найден")
    public void getAllAccountsBalanceInRubFailure() {
        String userName = "login";

        RestAssured.given(requestSpecification)
                .queryParam(REQUEST_PARAM_LOGIN, userName)
                .get(BALANCE_PATH)
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(USER_NOT_FOUND.formatted(userName)));
    }

    private User saveUser(UserDtoRq userDtoRq) {
        userService.saveUser(userDtoRq);
        assertTrue(userRepository.getUser(userDtoRq.getLogin()).isPresent());
       return userRepository.getUser(userDtoRq.getLogin()).get();
    }

    private Account createCryptoWalletBtc(User user) {
        UUID uuid = cryptoWalletService.createCryptoWallet(user.getLogin(), CryptoCoinType.BTC);
        assertTrue(accountRepository.getAccountByUuid(uuid).isPresent());
        return cryptoWalletService.findAccountByUuid(uuid);
    }

    private Account createCryptoWalletEth(User user) {
        UUID uuid = cryptoWalletService.createCryptoWallet(user.getLogin(), CryptoCoinType.ETH);
        assertTrue(accountRepository.getAccountByUuid(uuid).isPresent());
        return cryptoWalletService.findAccountByUuid(uuid);
    }

    private Account createCryptoWalletSol(User user) {
        UUID uuid = cryptoWalletService.createCryptoWallet(user.getLogin(), CryptoCoinType.SOL);
        assertTrue(accountRepository.getAccountByUuid(uuid).isPresent());
        return cryptoWalletService.findAccountByUuid(uuid);
    }

    private boolean compareBalance(BigDecimal expected, BigDecimal actual) {
        return expected.compareTo(actual) == 0;
    }
}
