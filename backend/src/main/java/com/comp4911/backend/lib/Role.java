package com.comp4911.backend.lib;

public enum Role {
    ADMIN("Administrator"),
    EMP("Employee"),
    HR("Human Resource");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.getValue().equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("No constant with role " + role + " found");
    }
}
