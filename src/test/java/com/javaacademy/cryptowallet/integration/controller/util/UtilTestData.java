package com.javaacademy.cryptowallet.integration.controller.util;

import com.javaacademy.cryptowallet.dto.CreateAccountDtoRq;
import com.javaacademy.cryptowallet.dto.ResetPasswordDtoRq;
import com.javaacademy.cryptowallet.dto.UserDtoRq;
import com.javaacademy.cryptowallet.model.user.User;

public class UtilTestData {
    public static UserDtoRq createUserDtoRq() {
        return new UserDtoRq(
                "test123",
                "test@example.com",
                "1234567890");
    }

    public static ResetPasswordDtoRq createResetPasswordDtoRq() {
        return new ResetPasswordDtoRq(
                "test123",
                "1234567890",
                "0987654321");
    }

    public static CreateAccountDtoRq createCreateAccountDtoRqWithBtc(UserDtoRq userDtoRq) {
        return new CreateAccountDtoRq(
                userDtoRq.getLogin(),
                "BTC");
    }

    public static CreateAccountDtoRq createCreateAccountDtoRqWithBtc(User user) {
        return new CreateAccountDtoRq(
                user.getLogin(),
                "BTC");
    }
}
