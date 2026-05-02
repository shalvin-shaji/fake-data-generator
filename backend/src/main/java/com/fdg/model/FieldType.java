package com.fdg.model;

/**
 * All field types the generator engine knows how to produce.
 * Each enum value is bound to a FieldGenerator implementation
 * via the FieldGeneratorRegistry.
 */
public enum FieldType {
    // Identifiers
    UUID,
    SEQUENCE,
    REFERENCE,

    // Primitives
    INTEGER,
    DECIMAL,
    BOOLEAN,
    STRING,

    // Personal
    FIRST_NAME,
    LAST_NAME,
    FULL_NAME,
    EMAIL,
    PHONE,
    DATE_OF_BIRTH,

    // Address
    STREET,
    CITY,
    STATE,
    COUNTRY,
    POSTAL_CODE,

    // Finance
    IBAN,
    ACCOUNT_NUMBER,
    CURRENCY,
    CUSIP,
    ISIN,
    TICKER,

    // Date/time
    DATE,
    TIMESTAMP,

    // Choice
    ENUM,
    WEIGHTED_ENUM,

    // Pattern
    REGEX
}
