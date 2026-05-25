package com.uber.kafka;


import com.uber.config.KafkaTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RideRequestProducer {
private final KafkaTemplate<String, String> kafkaTemplate;
public RideRequestProducer(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
 }
    public void sendRideRequest(String rideRequest) {
        kafkaTemplate.send(KafkaTopic.RIDE_REQUEST, rideRequest);
    }
}
