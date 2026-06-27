package com.example.demo.service;

import com.example.demo.calculator.MacdCalculator;
import com.example.demo.calculator.RsiCalculator;
import com.example.demo.model.IndicatorSnapshot;
import com.example.demo.model.TickEvent;
import com.example.demo.queue.MarketDataQueue;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class IndicatorService {
    private final MarketDataQueue queue;
    private final RsiCalculator rsiCalculator;
    private final MacdCalculator macdCalculator;
    private final AtomicReference<IndicatorSnapshot> latestSnapshot=new AtomicReference<>();

    private BigDecimal lastKnownPrice=BigDecimal.ZERO;

    public IndicatorService(MarketDataQueue queue,RsiCalculator rsiCalculator,MacdCalculator macdCalculator){
        this.queue=queue;
        this.rsiCalculator=rsiCalculator;
        this.macdCalculator=macdCalculator;
    }
    
    @Scheduled(fixedRate=1000)

    public void calculateIndicators(){
        TickEvent latestTick=queue.poll();

        if(latestTick!=null){
            lastKnownPrice=latestTick.getPrice();
        }else if (lastKnownPrice.compareTo(BigDecimal.ZERO) == 0) {
            // --- WEEKEND TEST MODE: Inject a fake AAPL price if queue is empty ---
            double randomFluctuation = (Math.random() * 2) - 1; // Random number between -1 and 1
            lastKnownPrice = new BigDecimal("180.50").add(new BigDecimal(randomFluctuation));
            // ---------------------------------------------------------------------
        }

        if(lastKnownPrice.compareTo(BigDecimal.ZERO)>0){
            
            IndicatorSnapshot snapshot=new IndicatorSnapshot(
                "AAPL",
                lastKnownPrice,
                rsiCalculator.calculate(lastKnownPrice),
                macdCalculator.calculate(lastKnownPrice),
                System.currentTimeMillis()
            );
            latestSnapshot.set(snapshot);
            System.out.println("[QUANT ENGINE] AAPL Price: $" + lastKnownPrice+" | RSI: "+String.format("%.2f",rsiCalculator.calculate(lastKnownPrice))+" | MACD: "+String.format("%.2f",macdCalculator.calculate(lastKnownPrice)));
        }
    }
    public IndicatorSnapshot getLatestSnapshot(){
        return latestSnapshot.get();
    }
}
