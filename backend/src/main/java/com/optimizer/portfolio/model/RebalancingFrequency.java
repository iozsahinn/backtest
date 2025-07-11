package com.optimizer.portfolio.model;

public enum RebalancingFrequency {
    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY"),
    QUARTERLY("QUARTERLY"),
    YEARLY("YEARLY");

    private final String value;

    RebalancingFrequency(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}