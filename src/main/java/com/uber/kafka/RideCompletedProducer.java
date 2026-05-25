package com.uber.kafka;

import com.uber.config.KafkaTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RideCompletedProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public RideCompletedProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRideCompleted(String message) {
        kafkaTemplate.send(KafkaTopic.RIDE_COMPLETED, message);
    }
}
