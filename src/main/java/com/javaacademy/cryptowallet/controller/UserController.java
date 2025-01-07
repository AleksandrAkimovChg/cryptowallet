package com.javaacademy.cryptowallet.controller;

import com.javaacademy.cryptowallet.dto.ResetPasswordDtoRq;
import com.javaacademy.cryptowallet.dto.UserDtoRq;
import com.javaacademy.cryptowallet.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "User controller",
        description = "API для создания пользователя или для смены пароля существующего пользователя")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Создание пользователя в системе",
            description = "Ресурс позволяет создать пользователя, "
                    + "передав в теле запроса атрибуты сущности пользователя")
    @ApiResponse(
            responseCode = "201",
            description = "Успешное создание пользователя",
            content = {@Content(schema = @Schema())}
    )
    @ApiResponse(
            responseCode = "400",
            description = "Неуспешное создание пользователя (Пользователь с таким логином уже существует)",
            content = {
                    @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserDtoRq userDto) {
        try {
            userService.saveUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @Operation(summary = "Смена пароля пользователя", description = "Ресурс позволяет сменить старый пароль "
            + "пользователя на новый")
    @ApiResponse(
            responseCode = "200",
            description = "Успешная смена пароля",
            content = {@Content(schema = @Schema())}
    )
    @ApiResponse(
            responseCode = "400",
            description = "Неуспешная смена пароля пользователя (Пользователь не найден / пароль не совпадает)",
            content = {
                    @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            }
    )
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDtoRq resetPasswordDto) {
        try {
            userService.changePassword(resetPasswordDto);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
