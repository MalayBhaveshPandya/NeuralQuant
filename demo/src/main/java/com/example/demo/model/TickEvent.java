package com.example.demo.model;

import java.math.BigDecimal;

public class TickEvent {
    private String symbol;
    private BigDecimal price;
    private long timestamp;
    private long volume;

    // Empty constructor
    public TickEvent() {
    }

    // Full constructor
    public TickEvent(String symbol, BigDecimal price, long timestamp, long volume) {
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp;
        this.volume = volume;
    }

    // Explicit Getters and Setters (Replaces Lombok @Data)
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }
}