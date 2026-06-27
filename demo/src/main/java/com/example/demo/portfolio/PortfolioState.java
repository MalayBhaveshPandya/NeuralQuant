package com.example.demo.portfolio;

import com.example.demo.model.Position;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PortfolioState {
    private final Map<String, Position> holdings=new ConcurrentHashMap<>();
    private BigDecimal cashBalance=new BigDecimal("100000.00");

    public void executeTrade(String symbol,String action,BigDecimal price){
        if("HOLD".equalsIgnoreCase(action))return;

        Position position=holdings.getOrDefault(symbol,new Position(symbol,0,BigDecimal.ZERO));
        int currQty=position.getQuantity();

        if("BUY".equalsIgnoreCase(action)){
            cashBalance=cashBalance.subtract(price.multiply(new BigDecimal("10")));
            position.setQuantity(currQty+10);
            position.setAveragePrice(price);
            holdings.put(symbol,position);
            System.out.println("[OMS] Executed BUY 10 " + symbol + " @ $" + price);
        }else if("SELL".equalsIgnoreCase(action) && currQty > 0){
            cashBalance=cashBalance.add(price.multiply(new BigDecimal("10")));
            position.setQuantity(Math.max(0,currQty-10));
            holdings.put(symbol,position);
            System.out.println("[OMS] Executed SELL 10 " + symbol + " @ $" + price);
        }
    }

    public Map<String, Position> getHoldings(){ return holdings;}
    public BigDecimal getCashBalance(){ return cashBalance;}
}
