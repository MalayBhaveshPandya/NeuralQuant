package com.example.demo.controller;

import com.example.demo.model.IndicatorSnapshot;
import com.example.demo.model.Position;
import com.example.demo.model.TradeSignal;
import com.example.demo.portfolio.PortfolioState;
import com.example.demo.service.AISignalService;
import com.example.demo.service.IndicatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SignalController {

    private final IndicatorService indicatorService;
    private final AISignalService aiSignalService;
    private final PortfolioState portfolioState;

    public SignalController(IndicatorService indicatorService, AISignalService aiSignalService, PortfolioState portfolioState) {
        this.indicatorService = indicatorService;
        this.aiSignalService = aiSignalService;
        this.portfolioState = portfolioState;
    }

    // 1. Trigger the AI to look at the latest math and make a decision
    @PostMapping("/signal/{symbol}")
    public ResponseEntity<TradeSignal> generateSignal(@PathVariable String symbol) {
        IndicatorSnapshot snapshot = indicatorService.getLatestSnapshot();
        if (snapshot == null) return ResponseEntity.badRequest().build();

        TradeSignal signal = aiSignalService.analyze(snapshot);
        
        // Auto-execute the mock trade in our Portfolio OMS
        portfolioState.executeTrade(symbol, signal.action(), snapshot.currentPrice());
        
        return ResponseEntity.ok(signal);
    }

    // 2. View current paper money and holdings
    @GetMapping("/portfolio")
    public ResponseEntity<Map<String, Object>> getPortfolio() {
        return ResponseEntity.ok(Map.of(
            "cashBalance", portfolioState.getCashBalance(),
            "holdings", portfolioState.getHoldings()
        ));
    }
}