package com.uber.kafka;

import com.uber.config.KafkaTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RideCancelledProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    public RideCancelledProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void sendRideCancelled(String rideCancelled) {
        kafkaTemplate.send(KafkaTopic.RIDE_CANCELLED, rideCancelled);
    }
}
