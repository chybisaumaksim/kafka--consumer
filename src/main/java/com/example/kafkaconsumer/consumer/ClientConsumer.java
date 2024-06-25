package com.example.kafkaconsumer.consumer;

import com.example.kafkaconsumer.dto.ClientDto;
import com.example.kafkaconsumer.mapper.ClientMapper;
import com.example.kafkaconsumer.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientConsumer {

    private final ClientRepository clientRepository;
    private final ClientMapper mapper;

    @KafkaListener(topics = "${kafka.topic.client}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consume(String clientPayload) throws Exception {
        log.info("Client information received: {} ", clientPayload);
        ClientDto clientDto = mapper.mapToClientDto(clientPayload);

        clientRepository.save(mapper.mapToClient(clientDto));
        log.info("Client information with ID={} has been saved", clientDto.getClientId());
    }

}
