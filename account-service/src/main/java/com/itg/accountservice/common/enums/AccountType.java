package com.itg.accountservice.common.enums;

public enum AccountType {
    S("Savings"),
    C("Checking");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}