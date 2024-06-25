package com.example.kafkaconsumer.mapper;

import com.example.kafkaconsumer.dto.ClientDto;
import com.example.kafkaconsumer.entity.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientMapper {

    private final ObjectMapper objectMapper;

    public ClientDto mapToClientDto(String message) throws Exception {
        return objectMapper.readValue(message, ClientDto.class);
    }

    public Client mapToClient(ClientDto clientDto) {
        var client = new Client();
        client.setId(clientDto.getClientId());
        client.setEmail(clientDto.getEmail());
        return client;
    }
}
