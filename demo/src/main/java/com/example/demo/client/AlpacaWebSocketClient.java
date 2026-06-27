package com.example.demo.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.model.TickEvent;
import com.example.demo.queue.MarketDataQueue;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.URI;

@Component
public class AlpacaWebSocketClient {
    @Value("${alpaca.api.key}")
    private String apiKey;
    @Value("${alpaca.api.secret}")
    private String apiSecret;
    @Value("${alpaca.api.stream-url}")
    private String streamUrl;

    private final MarketDataQueue marketDataQueue;
    private final ObjectMapper objectMapper=new ObjectMapper();
    private WebSocketClient client;

    public AlpacaWebSocketClient(MarketDataQueue marketDataQueue) {
        this.marketDataQueue = marketDataQueue;
    }

    @PostConstruct
    public void init(){
        try{
            this.client=new WebSocketClient(new URI(streamUrl)){
            @Override
            public void onOpen(ServerHandshake handshakedata){
                System.out.println("[Alpaca] Connected to socket.");
                String authMsg = String.format("{\"action\": \"auth\", \"key\": \"%s\", \"secret\": \"%s\"}", apiKey, apiSecret);
                send(authMsg);
            }
            @Override
            public void onMessage(String message){
                try{
                    JsonNode jsonNode=objectMapper.readTree(message);
                    if(jsonNode.isArray()){
                        for(JsonNode node:jsonNode){
                            if(node.has("msg")&&"authenticated".equals(node.get("msg").asText())){
                                System.out.println("[Alpaca] Authenticated! Subscribing to AAPL...");
                                send("{\"action\": \"subscribe\", \"trades\": [\"AAPL\"]}");
                            }else if(node.has("T")&&"t".equals(node.get("T").asText())){
                                TickEvent tick=new TickEvent(
                                    node.get("S").asText(),
                                    new BigDecimal(node.get("p").asText()),
                                    node.get("t").asLong(),
                                    node.get("s").asLong()
                                );
                                marketDataQueue.push(tick);
                                System.out.println("[Live Tick]"+tick.getSymbol()+"-> $" + tick.getPrice());
                            }
                        }
                    }
                }catch(Exception e){
                    System.err.println("Error parsing inbound stream: "+e.getMessage());
                }
            }
            @Override
            public void onClose(int code,String reason,boolean remote){
                System.out.println("[Alpaca] Connection closed.");
            }
            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };
        this.client.connect();
    }catch(Exception e){
            System.err.println("Error initializing WebSocket client: "+e.getMessage());
        }
    }
}
