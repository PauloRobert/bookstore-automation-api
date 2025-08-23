package com.demoqa.utils;

import com.github.javafaker.Faker;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class DataFactory {
    private static final Faker faker = new Faker(new Locale("en"));

    public static String username() {
        // username limpo e curto
        String base = faker.name().username().replaceAll("[^a-zA-Z0-9]", "");
        int rnd = ThreadLocalRandom.current().nextInt(1000, 9999);
        return (base.isEmpty() ? "user" : base) + rnd;
    }

    public static String password() {
        // forte o suficiente p/ API de demo (maiusc, minusc, número, símbolo, >=8)
        return "Aa1!" + faker.internet().password(8, 12, true, true)
                .replaceAll("\\s", "");
    }

    /**
     * Gera um username mais simples para casos específicos
     */
    public static String simpleUsername() {
        return "user" + ThreadLocalRandom.current().nextInt(10000, 99999);
    }

    /**
     * Gera uma senha com padrão específico para testes
     */
    public static String strongPassword() {
        String letters = faker.lorem().characters(4, 6, false);
        String numbers = String.valueOf(ThreadLocalRandom.current().nextInt(100, 999));
        String symbols = "!@#$";
        char symbol = symbols.charAt(ThreadLocalRandom.current().nextInt(symbols.length()));

        return "Test" + letters + numbers + symbol;
    }
}