package com.uber.kafka;

import com.uber.config.KafkaTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RideAcceptConsumer {
    @KafkaListener(topics =  KafkaTopic.RIDE_ACCEPTED,groupId = "uber-group")
    public void consumeRideRequest(String rideAccept) {
        System.out.println("Ride Accepted : " + rideAccept);
    }
}
