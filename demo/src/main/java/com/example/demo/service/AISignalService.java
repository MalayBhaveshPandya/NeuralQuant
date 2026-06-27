package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.model.IndicatorSnapshot;
import com.example.demo.model.TradeSignal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AISignalService {
    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate=new RestTemplate();
    private final ObjectMapper objectMapper=new ObjectMapper();

    public TradeSignal analyze(IndicatorSnapshot snapshot){
        if(snapshot==null) return null;

        String promptText = String.format(
            "You are a quantitative trading agent. Analyze this real-time data for %s: " +
            "Price=%.2f, RSI=%.2f, MACD=%.3f. " +
            "Rules: RSI > 70 is overbought (favor SELL), RSI < 30 is oversold (favor BUY). " +
            "Respond strictly in valid JSON format with NO markdown formatting, NO backticks, and exactly these three keys: " +
            "'action' (string: strictly 'BUY', 'SELL', or 'HOLD'), 'confidence' (number between 0.0 and 1.0), and 'reasoning' (brief string).",
            snapshot.symbol(), snapshot.currentPrice(), snapshot.rsi(), snapshot.macd()
        );

        String requestBody = String.format(
            "{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}", 
            promptText.replace("\"", "\\\"")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try{
            String response=restTemplate.postForObject(apiUrl + apiKey, request, String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            String rawJsonText = rootNode
                .path("candidates").get(0)
                .path("content")
                .path("parts").get(0)
                .path("text").asText();

            String cleanJson = rawJsonText.replace("```json", "").replace("```", "").trim();
            JsonNode signalNode = objectMapper.readTree(cleanJson);

            return new TradeSignal(
                signalNode.path("action").asText(),
                signalNode.path("confidence").asDouble(),
                signalNode.path("reasoning").asText(),
                System.currentTimeMillis()
            );
        }catch(Exception e){
            System.err.println("AI Agent parsing failure: " + e.getMessage());
            e.printStackTrace();
            return new TradeSignal("HOLD", 0.0, "System Error: Fallback to HOLD", System.currentTimeMillis());
        }
    }
}
