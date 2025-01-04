package com.javaacademy.cryptowallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenApi() {

        Contact myContact = new Contact()
                .name("Александр Акимов")
                .email("my.email@example.com");

        Info info = new Info()
                .title("API криптовалютного кошелька")
                .version("1.0")
                .description("Этот API предоставляет эндпоинты для создания пользователей криптовалютного кошелька, "
                        + "а также для управления кошельком пользователя: его создание, пополнение и снятие, получение "
                        + "информации о балансе. В текущей версии поддерживаются только три вида криптовалюты: "
                        + "BTC (bitcoin), ETH (ethereum), SOL (solana).")
                .contact(myContact);

        return new OpenAPI()
                .info(info);
    }
}
