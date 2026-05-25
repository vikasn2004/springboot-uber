package com.uber.kafka;

import com.uber.config.KafkaTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RideRequestConsumer {
    @KafkaListener(topics =  KafkaTopic.RIDE_REQUEST,groupId ="uber-group")
    public void rideRequestProducer(String rideRequest) {
        System.out.println("Ride Request : " + rideRequest);
    }
}
