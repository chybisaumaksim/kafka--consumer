package com.example.kafkaconsumer;

import com.example.kafkaconsumer.consumer.ClientConsumer;
import com.example.kafkaconsumer.dto.ClientDto;
import com.example.kafkaconsumer.entity.Client;
import com.example.kafkaconsumer.mapper.ClientMapper;
import com.example.kafkaconsumer.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientConsumerTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientConsumer clientConsumer;

    @Test
    void testConsumeForClient() throws Exception {
        String clientPayload = "{\n" + "  \"clientId\": 1,\n" + "  \"email\": \"mail@gmail.com\"\n" + "}";
        Client client = new Client(1L, "mail@gmail.com");
        ClientDto clientDto = new ClientDto(1L, "mail@gmail.com");
        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        when(clientMapper.mapToClientDto(clientPayload)).thenReturn(clientDto);
        when(clientMapper.mapToClient(clientDto)).thenReturn(client);

        clientConsumer.consume(clientPayload);

        verify(clientRepository, times(1)).save(clientCaptor.capture());
        verify(clientMapper, times(1)).mapToClientDto(clientPayload);
        verify(clientMapper, times(1)).mapToClient(clientDto);
        Client capturedClient = clientCaptor.getValue();
        assertEquals(1L, capturedClient.getId());
        assertEquals("mail@gmail.com", capturedClient.getEmail());
    }
}