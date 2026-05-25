package com.uber.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    public static final String RIDE_REQUEST   = "ride_request";
    public static final String RIDE_ACCEPTED  = "ride_accepted";
    public static final String RIDE_CANCELLED = "ride_cancelled";
    public static final String RIDE_COMPLETED = "ride_completed";

    @Bean
    public NewTopic ride_request() {
        return TopicBuilder.name(RIDE_REQUEST).partitions(3).replicas(1).build();
    }
    @Bean
    public NewTopic ride_accepted() {
        return TopicBuilder.name(RIDE_ACCEPTED).partitions(3).replicas(1).build();
    }
    @Bean
    public NewTopic ride_cancelled() {
        return TopicBuilder.name(RIDE_CANCELLED).partitions(3).replicas(1).build();
    }
    @Bean
    public NewTopic ride_completed() {
        return TopicBuilder.name(RIDE_COMPLETED).partitions(3).replicas(1).build();
    }
}