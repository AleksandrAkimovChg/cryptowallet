package com.javaacademy.cryptowallet.integration.controller;

import com.javaacademy.cryptowallet.dto.ResetPasswordDtoRq;
import com.javaacademy.cryptowallet.dto.UserDtoRq;
import com.javaacademy.cryptowallet.model.user.User;
import com.javaacademy.cryptowallet.repository.UserRepository;
import com.javaacademy.cryptowallet.service.UserService;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
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

import static com.javaacademy.cryptowallet.integration.controller.util.UtilTestData.createUserDtoRq;
import static com.javaacademy.cryptowallet.integration.controller.util.UtilTestData.createResetPasswordDtoRq;
import static com.javaacademy.cryptowallet.repository.UserRepository.USER_ALREADY_EXIST;
import static com.javaacademy.cryptowallet.service.UserService.PASSWORD_NOT_EQUALS;
import static com.javaacademy.cryptowallet.service.UserService.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("local")
@DisplayName("Тестирование контроллера UserController")
public class UserControllerTest {
    public static final String SIGNUP_PATH = "/signup";
    public static final String RESET_PASSWORD_PATH = "/reset-password";
    private final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBasePath("/user")
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

    @Test
    @DisplayName("Успешное создание пользователя")
    public void signupSuccess() {
        UserDtoRq userDtoRq = createUserDtoRq();
        assertTrue(userRepository.getUser(userDtoRq.getLogin()).isEmpty());

        RestAssured.given(requestSpecification)
                .body(userDtoRq)
                .post(SIGNUP_PATH)
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.CREATED.value());

        assertTrue(userRepository.getUser(userDtoRq.getLogin()).isPresent());
        User userActual = userRepository.getUser(userDtoRq.getLogin()).get();
        assertEquals(userDtoRq.getLogin(), userActual.getLogin());
        assertEquals(userDtoRq.getEmail(), userActual.getEmail());
        assertEquals(userDtoRq.getPassword(), userActual.getPassword());
    }

    @Test
    @DisplayName("Неуспешное повторное создание пользователя с совпадающим логином")
    public void signupFailure() {
        UserDtoRq userDtoRq = createUserDtoRq();
        assertTrue(userRepository.getUser(userDtoRq.getLogin()).isEmpty());
        saveUser(userDtoRq);

        RestAssured.given(requestSpecification)
                .body(userDtoRq)
                .post(SIGNUP_PATH)
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(USER_ALREADY_EXIST.formatted(userDtoRq.getLogin())));
    }

    @Test
    @DisplayName("Успешная смена пароля")
    public void resetPasswordSuccess() {
        User user = saveUser(createUserDtoRq());
        ResetPasswordDtoRq resetPasswordDtoRq = createResetPasswordDtoRq();
        assertEquals(user.getPassword(), resetPasswordDtoRq.getOldPassword());
        assertNotEquals(user.getPassword(), resetPasswordDtoRq.getNewPassword());

        RestAssured.given(requestSpecification)
                .body(resetPasswordDtoRq)
                .post(RESET_PASSWORD_PATH)
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.OK.value());
        User userActual = userRepository.getUser(user.getLogin()).get();

        assertEquals(user.getLogin(), userActual.getLogin());
        assertEquals(user.getEmail(), userActual.getEmail());
        assertEquals(resetPasswordDtoRq.getNewPassword(), userActual.getPassword());
    }

    @Test
    @DisplayName("Неуспешная смена пароля - пользователь не найден")
    public void resetPasswordFailureUserNotFound() {
        ResetPasswordDtoRq resetPasswordDtoRq = createResetPasswordDtoRq();
        assertTrue(userRepository.getUser(resetPasswordDtoRq.getLogin()).isEmpty());

        RestAssured.given(requestSpecification)
                .body(resetPasswordDtoRq)
                .post(RESET_PASSWORD_PATH)
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(USER_NOT_FOUND.formatted(resetPasswordDtoRq.getLogin())));
    }

    @Test
    @DisplayName("Неуспешная смена пароля - пароль не совпадает")
    public void resetPasswordFailurePasswordNotEquals() {
        User user = saveUser(createUserDtoRq());
        ResetPasswordDtoRq resetPasswordDtoRq = new ResetPasswordDtoRq(
                user.getLogin(),
                "test",
                "test");
        assertNotEquals(user.getPassword(), resetPasswordDtoRq.getOldPassword());

        RestAssured.given(requestSpecification)
                .body(resetPasswordDtoRq)
                .post(RESET_PASSWORD_PATH)
                .then()
                .spec(responseSpecification)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(PASSWORD_NOT_EQUALS));
    }

    private User saveUser(UserDtoRq userDtoRq) {
        userService.saveUser(userDtoRq);
        assertTrue(userRepository.getUser(userDtoRq.getLogin()).isPresent());
        return userRepository.getUser(userDtoRq.getLogin()).get();
    }
}
