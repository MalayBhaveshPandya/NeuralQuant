package com.example.demo.queue;

import com.example.demo.model.TickEvent;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentLinkedQueue;
@Component
public class MarketDataQueue {
    private final ConcurrentLinkedQueue<TickEvent>queue=new ConcurrentLinkedQueue<>();
    private static final int MAX_CAPACITY=500;

    public void push(TickEvent event){
        queue.offer(event);
        while(queue.size()>MAX_CAPACITY){
            queue.poll();
        }
    }
    public TickEvent poll(){
        return queue.poll();
    }
    public int size(){
        return queue.size();
    }
}
