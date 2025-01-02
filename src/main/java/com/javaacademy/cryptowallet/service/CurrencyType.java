package com.javaacademy.cryptowallet.service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CurrencyType {
    USD("usd");
    private final String name;
}
