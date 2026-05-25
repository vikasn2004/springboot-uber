package com.uber.kafka;

import com.uber.config.KafkaTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RideCompletedConsumer {
    @KafkaListener(topics = KafkaTopic.RIDE_COMPLETED,groupId = "uber-group")
    public void rideCompleted(String rideCompleted) {
        System.out.println("Ride Completed : " + rideCompleted);
    }
}
