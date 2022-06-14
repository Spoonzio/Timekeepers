package com.comp4911.backend.lib;

public enum ApproveStatus {
    UNAPPROVED((byte) 0, "unapproved"),
    APPROVED((byte) 1, "approved"),
    SUBMITTED((byte) 2, "submitted");

    private final String key;
    private final byte value;

    ApproveStatus(byte value, String key) {
        this.value = value;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public byte getValue() {
        return value;
    }

    public static ApproveStatus getStatus(byte value) {
        for (ApproveStatus status : ApproveStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }

    public static ApproveStatus getStatus(String key) {
        for (ApproveStatus status : ApproveStatus.values()) {
            if (status.getKey().equalsIgnoreCase(key)) {
                return status;
            }
        }
        return null;
    }
}
