package com.uber.kafka;

import com.uber.config.KafkaTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RideCancelledConsumer {
    @KafkaListener(topics = KafkaTopic.RIDE_CANCELLED,groupId = "uber-group")
    public void rideCancelled(String rideCancelled) {
        System.out.println("Ride Cancelled : " + rideCancelled);
    }
}
