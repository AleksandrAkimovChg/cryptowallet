package com.javaacademy.cryptowallet.parser;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class CryptoParser {

    public Optional<BigDecimal> parseValueByTemplate(String responseBody, String template) {
        try {
            return Optional.of(JsonPath.parse(responseBody).read(JsonPath.compile(template), BigDecimal.class));
        } catch (PathNotFoundException e) {
            log.warn(e.getMessage(), e);
        }
        return Optional.empty();
    }
}
