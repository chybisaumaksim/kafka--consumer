package com.example.kafkaconsumer.entity;

import com.example.kafkaconsumer.dto.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bank;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private TransactionType orderType;

    private Double totalCost;

    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "clientId", nullable = false)
    private Client client;
}
