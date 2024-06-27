package com.example.kafkaconsumer.unit;

import com.example.kafkaconsumer.consumer.TransactionConsumer;
import com.example.kafkaconsumer.dto.TransactionDto;
import com.example.kafkaconsumer.dto.TransactionType;
import com.example.kafkaconsumer.entity.Client;
import com.example.kafkaconsumer.entity.Transaction;
import com.example.kafkaconsumer.mapper.TransactionMapper;
import com.example.kafkaconsumer.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionConsumerTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionConsumer transactionConsumer;

    @Test
    void testConsumeForTransactions() throws JsonProcessingException {
        String transactionPayload =
                "{\n" + "  \"bank\": \"bank\",\n" + "  \"clientId\": 1,\n" + "  " + "\"transactionType\": \"INCOME\",\n" + "  \"quantity\": 0,\n" + "  \"price\": 0,\n" + "  " + "\"createdAt\": \"2024-06-24T14:37:28.336Z\"\n" + "}";
        Client client = new Client(1L, "mail@gmail.com");
        Transaction transaction =
                new Transaction(1L, "bank", TransactionType.INCOME, 6, 2.0, 12.0, LocalDateTime.now(), client);
        TransactionDto transactionDto =
                new TransactionDto("bank", 1L, TransactionType.INCOME, 1, 12.0, LocalDateTime.now());
        ArgumentCaptor<Transaction> transactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        when(transactionMapper.mapToTransactionDto(transactionPayload)).thenReturn(transactionDto);
        when(transactionMapper.mapToTransaction(transactionDto)).thenReturn(transaction);

        transactionConsumer.consume(transactionPayload);

        verify(transactionRepository, times(1)).save(transactionArgumentCaptor.capture());
        verify(transactionMapper, times(1)).mapToTransactionDto(transactionPayload);
        verify(transactionMapper, times(1)).mapToTransaction(transactionDto);
        Transaction argumentCaptorValue = transactionArgumentCaptor.getValue();
        assertEquals(1L, argumentCaptorValue.getId());
    }
}