package com.uber.kafka;

import com.uber.config.KafkaTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RideAcceptProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    public RideAcceptProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void rideAccept(String rideAccept) {
        kafkaTemplate.send(KafkaTopic.RIDE_ACCEPTED, rideAccept);
    }
}
