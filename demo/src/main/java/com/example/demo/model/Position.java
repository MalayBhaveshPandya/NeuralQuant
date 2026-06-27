package com.example.demo.model;

import java.math.BigDecimal;
public class Position {
    private String symbol;
    private int quantity;
    private BigDecimal averagePrice;

    public Position(String symbol, int quantity, BigDecimal averagePrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
    }

    public String getSymbol() {return symbol;}
    public void setSymbol(String symbol){ this.symbol=symbol;}
    public int getQuantity() {return quantity;}
    public void setQuantity(int quantity){ this.quantity=quantity;}
    public BigDecimal getAveragePrice() {return averagePrice;}
    public void setAveragePrice(BigDecimal averagePrice){ this.averagePrice=averagePrice;}
}
