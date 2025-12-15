package com.JavaEcommerce.Ecommerce.model;

public enum AddressType {
    SHIPPING("Shipping Address"),
    BILLING("Billing Address"),
    BOTH("Shipping & Billing Address");

    private final String displayName;

    AddressType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

