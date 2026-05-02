package com.fdg.generator.fieldtypes;

import java.util.List;
import java.util.Map;

/**
 * Utility for reading typed values from a FieldDefinition's free-form
 * config map. Each FieldGenerator uses this to pull out its own options
 * with safe defaults.
 */
public final class ConfigReader {

    private ConfigReader() {}

    public static long getLong(Map<String, Object> cfg, String key, long defaultValue) {
        Object v = cfg.get(key);
        if (v == null) return defaultValue;
        if (v instanceof Number n) return n.longValue();
        return Long.parseLong(v.toString());
    }

    public static double getDouble(Map<String, Object> cfg, String key, double defaultValue) {
        Object v = cfg.get(key);
        if (v == null) return defaultValue;
        if (v instanceof Number n) return n.doubleValue();
        return Double.parseDouble(v.toString());
    }

    public static int getInt(Map<String, Object> cfg, String key, int defaultValue) {
        Object v = cfg.get(key);
        if (v == null) return defaultValue;
        if (v instanceof Number n) return n.intValue();
        return Integer.parseInt(v.toString());
    }

    public static String getString(Map<String, Object> cfg, String key, String defaultValue) {
        Object v = cfg.get(key);
        return v == null ? defaultValue : v.toString();
    }

    public static boolean getBoolean(Map<String, Object> cfg, String key, boolean defaultValue) {
        Object v = cfg.get(key);
        if (v == null) return defaultValue;
        if (v instanceof Boolean b) return b;
        return Boolean.parseBoolean(v.toString());
    }

     @SuppressWarnings("unchecked")
    public static List<Object> getList(Map<String, Object> cfg, String key) {
        Object v = cfg.get(key);
        if (v == null) return List.of();
        if (v instanceof List<?> l) return (List<Object>) l;
        throw new IllegalArgumentException("Config '" + key + "' must be a list");
    }
}
