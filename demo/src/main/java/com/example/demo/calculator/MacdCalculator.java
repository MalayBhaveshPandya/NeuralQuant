package com.example.demo.calculator;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class MacdCalculator {
    private static final double EMA_12_MULTIPLIER=2.0/(12+1);
    private static final double EMA_26_MULTIPLIER=2.0/(26+1);
    private Double ema12=null;
    private Double ema26=null;

    public double calculate(BigDecimal latestPrice){
        double currentPrice=latestPrice.doubleValue();
        if(ema12==null){
            ema12=currentPrice;
            ema26=currentPrice;
            return 0.0;
        }

        ema12=((currentPrice-ema12)*EMA_12_MULTIPLIER)+ema12;
        ema26=((currentPrice-ema26)*EMA_26_MULTIPLIER)+ema26;

        return ema12-ema26;
    }
}
