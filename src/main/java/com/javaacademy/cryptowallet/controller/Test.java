package com.javaacademy.cryptowallet.controller;

import com.javaacademy.cryptowallet.model.account.CryptoCoinType;
import com.javaacademy.cryptowallet.service.integration.coin_price.CoinPriceService;
import com.javaacademy.cryptowallet.service.integration.course_rub.CourseRubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/test")
public class Test {
    private final CoinPriceService cryptoPriceService;
    private final CourseRubService courseRubService;

    @GetMapping("/rub")
    public BigDecimal getRub() {
        return courseRubService.getCourseRubToUsd();
    }

    @GetMapping("/price")
    public BigDecimal getPrice(@RequestParam CryptoCoinType coin) {
        return cryptoPriceService.getCoinPriceInUsd(coin);
    }
}
