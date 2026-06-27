package com.example.demo.model;

import java.math.BigDecimal;;
public record IndicatorSnapshot (
    String symbol,
    BigDecimal currentPrice,
    double rsi,
    double macd,
    long timestamp
){}
