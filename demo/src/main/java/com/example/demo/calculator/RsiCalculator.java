package com.example.demo.calculator;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.LinkedList;

@Component
public class RsiCalculator {
    private static final int PERIOD=14;
    private final LinkedList<Double>priceWindow=new LinkedList<>();

    public double calculate(BigDecimal latestPrice){
        double currentPrice=latestPrice.doubleValue();
        priceWindow.addLast(currentPrice);
        if(priceWindow.size()>PERIOD+1){
            priceWindow.removeFirst();
        }
        if(priceWindow.size()<PERIOD+1){
            return 50.0;
        }
        double gains=0.0;
        double losses=0.0;
        for(int i=1;i<priceWindow.size();i++){
            double difference=priceWindow.get(i)-priceWindow.get(i-1);
            if(difference>=0) gains+=difference;
            else losses-=difference;
        }
        double avgGain=gains/PERIOD;
        double avgLoss=losses/PERIOD;

        if(avgLoss==0) return 100.0;
        double rs=avgGain/avgLoss;
        return 100.0-(100.0/(1.0+rs));
    }
}
