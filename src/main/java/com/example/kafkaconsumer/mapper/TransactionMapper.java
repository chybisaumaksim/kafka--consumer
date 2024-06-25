package com.example.kafkaconsumer.mapper;

import com.example.kafkaconsumer.dto.TransactionDto;
import com.example.kafkaconsumer.entity.Transaction;
import com.example.kafkaconsumer.repository.ClientRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionMapper {

    private final ObjectMapper objectMapper;
    private final ClientRepository clientRepository;

    public TransactionDto mapToTransactionDto(String message) throws JsonProcessingException {
        return objectMapper.readValue(message, TransactionDto.class);
    }

    public Transaction mapToTransaction(TransactionDto transactionDto) {
        return Transaction.builder()
                .bank(transactionDto.getBank())
                .client(clientRepository.getReferenceById(transactionDto.getClientId()))
                .totalCost(transactionDto.getQuantity() * transactionDto.getPrice())
                .orderType(transactionDto.getTransactionType())
                .createdAt(transactionDto.getCreatedAt())
                .build();
    }
}
