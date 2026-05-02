package com.fdg.generator.fieldtypes;

import com.fdg.generator.FieldGenerator;
import com.fdg.generator.GenerationContext;
import com.fdg.model.FieldDefinition;
import com.fdg.model.FieldType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Finance-domain field generators. */
public final class FinanceGenerators {

    @Component
    public static class IbanGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.IBAN; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            return c.getFaker().finance().iban();
        }
    }

    /** Random N-digit account number. Config: length (int, default 10). */
    @Component
    public static class AccountNumberGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.ACCOUNT_NUMBER; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            int len = ConfigReader.getInt(f.getConfig(), "length", 10);
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                sb.append(ThreadLocalRandom.current().nextInt(10));
            }
            return sb.toString();
        }
    }

    @Component
    public static class CurrencyGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.CURRENCY; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            return c.getFaker().money().currencyCode();
        }
    }

    /** 9-character CUSIP (8 alphanumeric + 1 check digit). */
    @Component
    public static class CusipGenerator implements FieldGenerator {
        private static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        @Override public FieldType type() { return FieldType.CUSIP; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            StringBuilder sb = new StringBuilder(9);
            for (int i = 0; i < 9; i++) {
                sb.append(ALPHA.charAt(ThreadLocalRandom.current().nextInt(ALPHA.length())));
            }
            return sb.toString();
        }
    }

    /** 12-character ISIN: 2-letter country + 9 alphanumeric + 1 check digit. */
    @Component
    public static class IsinGenerator implements FieldGenerator {
        private static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        @Override public FieldType type() { return FieldType.ISIN; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            String country = c.getFaker().country().countryCode2().toUpperCase();
            StringBuilder sb = new StringBuilder(12).append(country);
            for (int i = 0; i < 10; i++) {
                sb.append(ALPHA.charAt(ThreadLocalRandom.current().nextInt(ALPHA.length())));
            }
            return sb.toString();
        }
    }

    /** Picks from a configurable ticker list, with a default common-stocks list. */
    @Component
    public static class TickerGenerator implements FieldGenerator {
        private static final List<String> DEFAULTS = List.of(
                "AAPL","MSFT","GOOGL","AMZN","META","TSLA","NVDA","JPM","V","WMT",
                "JNJ","PG","UNH","HD","MA","DIS","BAC","XOM","ADBE","NFLX");
        @Override public FieldType type() { return FieldType.TICKER; }
        @Override
        public Object generate(FieldDefinition f, GenerationContext c) {
            List<Object> tickers = ConfigReader.getList(f.getConfig(), "values");
            List<?> source = tickers.isEmpty() ? DEFAULTS : tickers;
            return source.get(ThreadLocalRandom.current().nextInt(source.size())).toString();
        }
    }
}
