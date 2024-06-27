package com.example.kafkaconsumer.integration;

import com.example.kafkaconsumer.mapper.TransactionMapper;
import com.example.kafkaconsumer.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
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
@Testcontainers
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = { "${kafka.topic.transaction}" })
public class TransactionConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

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

    @BeforeEach
    void setUp() {
        var clientPayload = "{\"clientId\":1, \"email\":\"mail@gmail.com\"}";
        kafkaTemplate.send("client-topic", clientPayload);
    }

    @Test
    public void testConsumeTransactions() throws Exception {
        var transactionPayload =
                "{\n" + "  \"bank\": \"PKO\",\n"
                        + "  \"clientId\": 1,\n"
                        + "  \"transactionType\": \"INCOME\",\n"
                        + "  \"quantity\": 2,\n" + "  \"price\": 2,\n"
                        + "  \"createdAt\": \"2024-06-26T10:28:33.910Z\"\n"
                        + "}";

        var transactionDto = transactionMapper.mapToTransactionDto(transactionPayload);
        System.out.println(transactionDto.getBank());

        kafkaTemplate.send("transaction-topic", transactionPayload);

        Thread.sleep(2000);

        var savedTransaction = transactionRepository.findById(2L)
                .orElse(null);
        assertThat(savedTransaction).isNotNull();
        assertThat(savedTransaction.getBank()).isEqualTo(transactionDto.getBank());
    }
}