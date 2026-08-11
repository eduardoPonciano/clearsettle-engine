package com.eduardoponciano.clearsettle.infrastructure.adapter.inbound.rest;

import com.eduardoponciano.clearsettle.domain.model.SettlementStatus;
import com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence.jpa.SpringDataJpaSettlementRepository;
import com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence.mongo.SpringDataMongoSettlementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class SettlementIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SpringDataJpaSettlementRepository jpaRepository;

    @Autowired
    private SpringDataMongoSettlementRepository mongoRepository;

    @Test
    @DisplayName("Should process settlement end-to-end: REST -> Kafka -> Worker -> Database")
    void fullFlowIntegrationTest() {
        // 1. Prepare Request
        UUID txId = UUID.randomUUID();
        SettlementController.SettlementRequest request = new SettlementController.SettlementRequest(
                txId,
                new BigDecimal("250.75"),
                "BRL",
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now()
        );

        // 2. Execute REST Call
        ResponseEntity<Map> response = restTemplate.postForEntity("/v1/transactions/settlement", request, Map.class);

        // 3. Verify API Acceptance
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody().get("status")).isEqualTo("ACCEPTED");

        // 4. Wait for Asynchronous Processing (Worker)
        await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    // Verify PostgreSQL State
                    var jpaEntity = jpaRepository.findById(txId);
                    assertThat(jpaEntity).isPresent();
                    assertThat(jpaEntity.get().getStatus()).isEqualTo(SettlementStatus.COMPLETED);
                    
                    // Verify MongoDB Audit Log
                    boolean auditExists = mongoRepository.existsByTransactionId(txId);
                    assertThat(auditExists).isTrue();
                });
    }
}
