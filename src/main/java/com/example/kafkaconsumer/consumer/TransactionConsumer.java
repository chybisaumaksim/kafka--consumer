package com.example.kafkaconsumer.consumer;

import com.example.kafkaconsumer.dto.TransactionDto;
import com.example.kafkaconsumer.mapper.TransactionMapper;
import com.example.kafkaconsumer.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionConsumer {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper mapper;

    @SneakyThrows
    @KafkaListener(topics = "${kafka.topic.transaction}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consume(String transactionPayload) {
        log.info("Transaction information received: {} ", transactionPayload);
        TransactionDto transactionDto = mapper.mapToTransactionDto(transactionPayload);

        transactionRepository.save(mapper.mapToTransaction(transactionDto));
        log.info("Transaction information for client with ID={} has been saved", transactionDto.getClientId());
    }
}
