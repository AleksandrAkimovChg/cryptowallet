package com.javaacademy.cryptowallet.controller;

import com.javaacademy.cryptowallet.model.CryptoCoin;
import com.javaacademy.cryptowallet.service.course_service.CourseService;
import com.javaacademy.cryptowallet.service.coin_price_service.CryptoPriceServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/test")
public class Test {
    private final CourseService courseService;
    private final CryptoPriceServiceImpl cryptoPriceServiceImpl;


    @GetMapping("/rub")
    public BigDecimal getRub(@RequestParam BigDecimal rub) {
        log.info("rub input: {}", rub);
        return courseService.convertRubToUsd(rub);
    }

    @GetMapping("/usd")
    public BigDecimal getUsd(@RequestParam BigDecimal usd) {
        log.info("usd input: {}", usd);
        return courseService.convertUsdToRub(usd);
    }

    @GetMapping("/course")
    public BigDecimal getCourse() {
        log.info("LocalDateTime: {}", LocalDateTime.now());
        return courseService.getCourseRubToUsd();
    }

    @GetMapping("/price")
    public BigDecimal getPrice() {
        log.info("LocalDateTime: {}", LocalDateTime.now());
        return cryptoPriceServiceImpl.getCoinPriceInUsd(CryptoCoin.SOL);
    }
}
