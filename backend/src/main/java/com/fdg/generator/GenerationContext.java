package com.fdg.generator;

import net.datafaker.Faker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GenerationContext {

    private final Faker faker;
    private final Map<String, AtomicLong> sequences = new ConcurrentHashMap<>();
    private final Map<String, ReferencePool> referencePools = new ConcurrentHashMap<>();

    public GenerationContext() {
        this.faker = new Faker();
    }

    public Faker getFaker() { return faker; }
    public Map<String, AtomicLong> getSequences() { return sequences; }
    public Map<String, ReferencePool> getReferencePools() { return referencePools; }

    public long nextSequence(String key, long start) {
        return sequences.computeIfAbsent(key, k -> new AtomicLong(start)).getAndIncrement();
    }

    public ReferencePool getOrCreatePool(String key, int capacity) {
        return referencePools.computeIfAbsent(key, k -> new ReferencePool(capacity));
    }
}
