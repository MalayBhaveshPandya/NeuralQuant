package com.example.demo.controller;

import com.example.demo.model.IndicatorSnapshot;
import com.example.demo.service.IndicatorService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/indicators")
@CrossOrigin(origins = "*")

public class IndicatorController {
    private final IndicatorService indicatorService;

    public IndicatorController(IndicatorService indicatorService){
        this.indicatorService=indicatorService;
    }

    @GetMapping("/AAPL")
    public ResponseEntity<IndicatorSnapshot> getAaplIndicators(){
        IndicatorSnapshot snapshot=indicatorService.getLatestSnapshot();
        if(snapshot==null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(snapshot);
    }
}
