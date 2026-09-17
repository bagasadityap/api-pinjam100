package com.bagas.pinjam100.service.notification;

public enum NotificationTopic {
    COMMON("common"),
    PROMO("promo");

    private final String value;

    NotificationTopic(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}