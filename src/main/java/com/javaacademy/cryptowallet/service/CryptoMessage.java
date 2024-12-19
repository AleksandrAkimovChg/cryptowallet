package com.javaacademy.cryptowallet.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CryptoMessage {
    private Boolean isSuccessful;
    private String text;
}
