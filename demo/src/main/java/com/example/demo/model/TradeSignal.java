package com.example.demo.model;

public record TradeSignal (
    String action,
    double confidence,
    String reasoning,
    long timestamp
){}
