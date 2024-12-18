package com.javaacademy.cryptowallet.controller;

import com.javaacademy.cryptowallet.dto.ResetPasswordDtoRq;
import com.javaacademy.cryptowallet.dto.UserDtoRq;
import com.javaacademy.cryptowallet.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void signup(@RequestBody UserDtoRq userDto) {
        log.info("userDto input: {}", userDto);
        userService.saveUser(userDto);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordDtoRq resetPasswordDto) {
        log.info("UserDto input: {}", resetPasswordDto);
        userService.changePassword(resetPasswordDto);
    }
}
