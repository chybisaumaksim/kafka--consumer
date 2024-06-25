package com.example.kafkaconsumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.Properties;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groudId;

    @Bean
    public KafkaConsumer<Long, String> clientKafkaConsumer() {
        return new KafkaConsumer<>(getConsumerProperties("clientGroup"));
    }

    @Bean
    public KafkaConsumer<Long, String> transactionKafkaConsumer() {
        return new KafkaConsumer<>(getConsumerProperties("transactionGroup"));
    }

    private Properties getConsumerProperties(String groupId) {
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return consumerProperties;
    }
}
