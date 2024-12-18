package com.javaacademy.cryptowallet.service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Currency {
    USD("usd");
    private final String name;
}
