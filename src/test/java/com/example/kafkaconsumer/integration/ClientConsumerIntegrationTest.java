package com.example.kafkaconsumer.integration;

import com.example.kafkaconsumer.mapper.ClientMapper;
import com.example.kafkaconsumer.repository.ClientRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"${kafka.topic.client}"})
@Testcontainers
@ActiveProfiles("test")
public class ClientConsumerIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:13-alpine").withDatabaseName("kafka")
                    .withUsername("user")
                    .withPassword("1234");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Test
    void consume_validClientPayload_savesClient() throws JsonProcessingException, InterruptedException {
        var clientPayload = "{\"clientId\":1, \"email\":\"mail@gmail.com\"}";
        var clientDto = clientMapper.mapToClientDto(clientPayload);
        kafkaTemplate.send("client-topic", clientPayload);
        Thread.sleep(2000);

        var savedClient = clientRepository.findById(clientDto.getClientId())
                .orElse(null);
        assertThat(savedClient).isNotNull();
        assertThat(savedClient.getEmail()).isEqualTo(clientDto.getEmail());
    }
}